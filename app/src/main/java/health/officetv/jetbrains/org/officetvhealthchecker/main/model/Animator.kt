package health.officetv.jetbrains.org.officetvhealthchecker.main.model

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar

abstract class AnimListener : android.animation.Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: android.animation.Animator?) = Unit
    override fun onAnimationEnd(animation: android.animation.Animator?) = Unit
    override fun onAnimationCancel(animation: android.animation.Animator?) = Unit
    override fun onAnimationStart(animation: android.animation.Animator?) = Unit
}

interface Animator {
    fun animate()
}

abstract class ViewAnimator(protected val target: View) : Animator

class ProgressBarAnimator(target: ProgressBar) : ViewAnimator(target) {
    override fun animate() {
        target.animate().setDuration(200)
            .scaleY(0f).scaleX(0f)
            .setListener(object : AnimListener() {
                override fun onAnimationEnd(animation: android.animation.Animator?) {
                    target.visibility = View.GONE
                }
            }).start()
    }
}

class ResultViewAnimator(target: ImageView): ViewAnimator(target) {
    override fun animate() {
        target.visibility = View.VISIBLE
        target.animate().setDuration(200)
            .scaleX(1f).scaleY(1f)
            .start()
    }
}