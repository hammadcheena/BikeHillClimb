package com.example.bikehillclimb

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class GameView(context: Context) : View(context) {
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bike = Paint(Paint.ANTI_ALIAS_FLAG)
    private var x = 260f
    private var y = 360f
    private var vx = 0f
    private var vy = 0f
    private var angle = 0f
    private var gas = false
    private var brake = false
    private var gameOver = false
    private var score = 0
    private var distance = 0f
    private var last = System.nanoTime()
    private val terrain = ArrayList<Float>()

    init {
        p.typeface = Typeface.DEFAULT_BOLD
        for (i in 0..100) terrain.add(430f - sin(i * .65) * 65f - sin(i * .19) * 45f)
    }

    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        val w = width.toFloat()
        val h = height.toFloat()
        c.drawColor(Color.rgb(190,225,250))

        // sun
        p.color = Color.YELLOW
        c.drawCircle(w - 90, 75f, 38f, p)

        // terrain
        p.color = Color.rgb(95,165,75)
        val path = Path()
        path.moveTo(0f, h)
        for (i in 0 until terrain.size) {
            val xx = i * 90f - (distance % 90f)
            val yy = terrain[i]
            if (i == 0) path.lineTo(xx, yy) else path.lineTo(xx, yy)
        }
        path.lineTo(w, h)
        path.close()
        c.drawPath(path, p)

        // bike
        drawBike(c)

        p.color = Color.BLACK
        p.textSize = 28f
        c.drawText("Score: $score", 24f, 42f, p)
        c.drawText("Distance: ${distance.toInt()} m", 24f, 78f, p)

        // controls
        p.color = Color.argb(130, 30,30,30)
        c.drawRoundRect(25f,h-105f,180f,h-25f,20f,20f,p)
        c.drawRoundRect(w-180f,h-105f,w-25f,h-25f,20f,20f,p)
        p.color = Color.WHITE
        p.textSize = 25f
        c.drawText("BRAKE", 55f,h-58f,p)
        c.drawText("GAS", w-140f,h-58f,p)

        if (gameOver) {
            p.color = Color.argb(190,0,0,0)
            c.drawRect(0f,0f,w,h,p)
            p.color = Color.WHITE
            p.textSize = 52f
            c.drawText("GAME OVER", w/2-170f,h/2-35f,p)
            p.textSize = 28f
            c.drawText("Tap the screen to restart", w/2-165f,h/2+25f,p)
        } else {
            update()
        }
        postInvalidateDelayed(16)
    }

    private fun update() {
        val now = System.nanoTime()
        val dt = ((now-last)/1_000_000_000.0).coerceAtMost(.04).toFloat()
        last = now
        if (gas) vx += 210f * dt
        if (brake) vx -= 150f * dt
        vx *= .992f
        vx = vx.coerceIn(0f, 430f)
        vy += 700f * dt
        y += vy * dt
        x += vx * dt
        distance += vx * dt / 8f
        score = distance.toInt()

        val ground = 350f - sin((distance/90f)*.65)*65f - sin((distance/90f)*.19)*45f
        if (y > ground - 48f) {
            y = ground - 48f
            vy = 0f
        }
        angle = -0.18f * sin((distance/90f)*.65) 
        if (x > width * .58f) x = width * .58f
        if (distance > 15_000f) gameOver = true
    }

    private fun drawBike(c: Canvas) {
        c.save()
        c.translate(x,y)
        c.rotate(angle*57.3f)
        p.color = Color.DKGRAY
        c.drawCircle(-42f,28f,23f,p)
        c.drawCircle(42f,28f,23f,p)
        p.color = Color.WHITE
        c.drawCircle(-42f,28f,9f,p)
        c.drawCircle(42f,28f,9f,p)
        p.color = Color.rgb(210,45,45)
        p.strokeWidth = 10f
        c.drawLine(-42f,28f,0f,-12f,p)
        c.drawLine(0f,-12f,42f,28f,p)
        c.drawLine(0f,-12f,-12f,28f,p)
        p.strokeWidth = 6f
        c.drawLine(0f,-12f,18f,-30f,p)
        p.color = Color.rgb(30,30,30)
        c.drawCircle(8f,-48f,13f,p)
        c.restore()
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            if (gameOver) {
                reset()
                return true
            }
            if (e.x > width * .55f) gas = true else brake = true
        } else if (e.action == MotionEvent.ACTION_UP || e.action == MotionEvent.ACTION_CANCEL) {
            gas = false
            brake = false
        }
        return true
    }

    private fun reset() {
        x = 260f; y = 360f; vx = 0f; vy = 0f
        distance = 0f; score = 0; gameOver = false
        last = System.nanoTime()
    }
}
