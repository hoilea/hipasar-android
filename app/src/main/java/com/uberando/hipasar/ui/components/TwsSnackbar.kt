package com.uberando.hipasar.ui.components

import android.view.View
import com.google.android.material.snackbar.Snackbar

class TwsSnackbar constructor(
  private val view: View,
  private val text: String,
  private val duration: Int
) {

  private var snackbar: Snackbar? = null

  constructor(view: View, text: String, duration: Int, backgroundColor: Int) : this(view, text, duration) {
    makeSnackbar(view, text, duration).apply {
      this.view.setBackgroundColor(backgroundColor)
    }
  }

  constructor(view: View, text: String, duration: Int, actionText: String, action: () -> Unit) : this(view, text, duration) {
    makeSnackbar(view, text, duration).apply {
      setAction(actionText) { action() }
    }
  }

  constructor(view: View, text: String, duration: Int, backgroundColor: Int, actionText: String, action: () -> Unit) : this(view, text, duration) {
    makeSnackbar(view, text, duration).apply {
      this.view.setBackgroundColor(backgroundColor)
      setAction(actionText) { action() }
    }
  }

  private fun makeSnackbar(view: View, text: String, duration: Int) =
    Snackbar.make(view, text, duration)

  fun show(): Snackbar {
    if (snackbar == null) {
      snackbar = makeSnackbar(view, text, duration)
    }
    snackbar?.show()
    return snackbar!!
  }

}