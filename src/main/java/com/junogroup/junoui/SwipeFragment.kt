package com.junogroup.junoui

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment

/**
 * The JSwipeFragment enables a drawer menu to be pulled from the bottom of the screen.
 * This fragment is, as the name implies, gesture controlled and is intended for the user to use
 * when creating a new element.
 *
 * TODO: Consider if the fragment should create a dim overlay of the activity
 *
 * @author Marko Vejnovic <marko.vejnovic@hotmail.com>
 */
abstract class JSwipeFragment : Fragment() {
    companion object {
        private val TAG = JSwipeFragment::class.qualifiedName
    }

    private lateinit var baseLayout: ViewGroup

    private var isClosing: Boolean = false

    private var previousFingerPosition: Int = 0

    /**
     * The getBaseView function enables you to inflate your ViewGroup for the view to use.
     */
    abstract fun getBaseView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        baseLayout = getBaseView(inflater, container, savedInstanceState)
        if (baseLayout.background == null) {
            baseLayout.background =
                ResourcesCompat.getDrawable(resources,
                    R.drawable.jswipefragment_default_background, null) // TODO: Set theme
        }

        baseLayout.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                try {
                    val y: Int = event!!.rawY.toInt()

                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            previousFingerPosition = y
                        }
                        MotionEvent.ACTION_UP -> {
                            if (baseLayout.y > baseLayout.height / 2) {
                                ValueAnimator.ofFloat(baseLayout.y, baseLayout.height.toFloat())
                                    .apply {
                                        duration = 300

                                        addUpdateListener { v: ValueAnimator? ->
                                            baseLayout.y = v?.animatedValue as Float
                                            baseLayout.requestLayout()
                                        }

                                        start()
                                    }
                            } else {
                                ValueAnimator.ofFloat(baseLayout.y, 0f).apply {
                                    duration = 300

                                    addUpdateListener { v: ValueAnimator? ->
                                        baseLayout.y = v?.animatedValue as Float
                                        baseLayout.requestLayout()
                                    }

                                    start()
                                }
                            }
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (!isClosing) {
                                if (previousFingerPosition < y) { // Scrolling down
                                    baseLayout.y = baseLayout.y + y - previousFingerPosition
                                    baseLayout.requestLayout()
                                } else { // Scrolling up
                                    if (baseLayout.y > 0f) {
                                        baseLayout.y = baseLayout.y + y - previousFingerPosition
                                        baseLayout.requestLayout()
                                    }
                                }
                                previousFingerPosition = y
                            }
                        }
                    }

                    return true
                } catch (npe: NullPointerException) {
                    Log.e(TAG, "MotionEvent is null.")
                    return false
                }
            }
        })

        return baseLayout
    }
}
