package it.sephiroth.android.library.uigestures.demo.fragments

import androidx.fragment.app.Fragment
import it.sephiroth.android.library.uigestures.UIGestureRecognizer
import java.lang.ref.WeakReference

/**
 * Copyright 2017 Adobe Systems Incorporated.  All rights reserved.
 * $Id$
 * $DateTime$
 * $Change$
 * $File$
 * $Revision$
 * $Author$
 */
abstract class IRecognizerFragment<T>(val recognizer: WeakReference<UIGestureRecognizer>) : Fragment() {

    abstract fun getRecognizerStatus(): String?

    fun getRecognizer(): T? {
        return (recognizer.get() as T)
    }
}