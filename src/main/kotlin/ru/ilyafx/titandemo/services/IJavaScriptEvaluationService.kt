package ru.ilyafx.titandemo.services

interface IJavaScriptEvaluationService {

    fun eval(script: String, value: Int): Int

}