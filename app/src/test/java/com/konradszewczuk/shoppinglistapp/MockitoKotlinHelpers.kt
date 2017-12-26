package com.konradszewczuk.shoppinglistapp

import org.mockito.ArgumentCaptor


fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()