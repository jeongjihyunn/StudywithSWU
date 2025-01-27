import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // XML 레이아웃 파일과 연결

        // 타임테이블 셀 추가
        val gridLayout = findViewById<GridLayout>(R.id.timetableGrid)
        gridLayout.removeAllViews()

        val rowCount = 7
        val columnCount = 24

        for (row in 0 until rowCount) {
            for (col in 0 until columnCount) {
                val textView = TextView(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        rowSpec = GridLayout.spec(row, 1f)
                        columnSpec = GridLayout.spec(col, 1f)
                        width = 0
                        height = 0
                    }
                    gravity = Gravity.CENTER
                    textSize = 12f

                    // 첫 번째 행에 시간 표시
                    if (row == 0) {
                        text = if (col == 0) "" else "${col - 1}:00"
                        setBackgroundColor(Color.LTGRAY)
                    } else {
                        // 나머지 행은 빈 셀
                        text = ""
                        setBackgroundColor(Color.WHITE)
                    }
                }
                gridLayout.addView(textView)
            }
        }
    }
}
