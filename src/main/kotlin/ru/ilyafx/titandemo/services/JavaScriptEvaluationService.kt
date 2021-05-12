package ru.ilyafx.titandemo.services

import org.springframework.stereotype.Service
import javax.script.Invocable
import javax.script.ScriptEngineManager

@Service
class JavaScriptEvaluationService : IJavaScriptEvaluationService {

    private val scriptManager = ScriptEngineManager()

    override fun eval(script: String, value: Int): Int {
        val javascriptEngine = scriptManager.getEngineByName("JavaScript")
        javascriptEngine.eval(script)
        return ((javascriptEngine as Invocable).invokeFunction("eval", value) as Double).toInt()
    }

}