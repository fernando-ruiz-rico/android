package com.example.composecharts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composecharts.ui.theme.ComposeChartsTheme
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie
import ir.ehsannarmani.compose_charts.models.PopupProperties

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ComposeChartsTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
              .verticalScroll(
                rememberScrollState()
              )
          ) {
            PieExample()
            LinesExample()
            ColumnsExample()
          }
        }
      }
    }
  }
}

@Composable
fun PieExample() {
  var data by remember {
    mutableStateOf(
      listOf(
        Pie(label = "Android", data = 20.0, color = Color.Red, selectedColor = Color.Green),
        Pie(label = "Windows", data = 45.0, color = Color.Cyan, selectedColor = Color.Blue),
        Pie(label = "Linux", data = 35.0, color = Color.Gray, selectedColor = Color.Yellow),
      )
    )
  }
  PieChart(
    modifier = Modifier.size(300.dp),
    data = data,
    onPieClick = {
      println("${it.label} Clicked")
      val pieIndex = data.indexOf(it)
      data = data.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
    },
    selectedScale = 1.2f,
    scaleAnimEnterSpec = spring<Float>(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow
    ),
    colorAnimEnterSpec = tween(300),
    colorAnimExitSpec = tween(300),
    scaleAnimExitSpec = tween(300),
    spaceDegreeAnimExitSpec = tween(300),
    style = Pie.Style.Fill,
    labelHelperProperties = LabelHelperProperties(
      enabled = true,
    )
  )
}

@Composable
fun LinesExample() {
  LineChart(
    modifier = Modifier.size(300.dp),
    data = remember {
      listOf(
        Line(
          label = "Windows",
          values = listOf(28.0, 41.0, 5.0, 10.0, 35.0),
          color = SolidColor(Color.Green),
          firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
          secondGradientFillColor = Color.Transparent,
          strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
          gradientAnimationDelay = 1000,
          drawStyle = DrawStyle.Stroke(width = 2.dp),
        ),
        Line(
          label = "Android",
          values = listOf(13.0, 21.0, 18.0, 14.0, 23.0),
          color = SolidColor(Color.Red),
          firstGradientFillColor = Color(0xFFC3A1A1).copy(alpha = .5f),
          secondGradientFillColor = Color.Transparent,
          strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
          gradientAnimationDelay = 1000,
          drawStyle = DrawStyle.Stroke(width = 2.dp),
        )
      )
    },
    animationMode = AnimationMode.Together(delayBuilder = {
      it * 500L
    }),
    indicatorProperties = HorizontalIndicatorProperties(
      contentBuilder = { indicator ->
        "%.2f".format(indicator) + "M"
      }
    ),
    popupProperties = PopupProperties(
      enabled = true,
      containerColor = Color.White,
      contentBuilder = { popup ->
        "%.2f".format(popup.value) + "M"
      }
    )
  )
}

@Composable
fun ColumnsExample() {
  ColumnChart(
    modifier = Modifier.size(300.dp),
    data = remember {
      listOf(
        Bars(
          label = "Jan",
          values = listOf(
            Bars.Data(label = "Linux", value = 50.0, color = SolidColor(Color.Blue)),
            Bars.Data(label = "Windows", value = 70.0, color = SolidColor(Color.Red))
          ),
        ),
        Bars(
          label = "Feb",
          values = listOf(
            Bars.Data(label = "Linux", value = 80.0, color = SolidColor(Color.Blue)),
            Bars.Data(label = "Windows", value = 60.0, color = SolidColor(Color.Red))
          ),
        ),
        Bars(
          label = "Mar",
          values = listOf(
            Bars.Data(label = "Linux", value = 74.0, color = SolidColor(Color.Blue)),
            Bars.Data(label = "Windows", value = 71.0, color = SolidColor(Color.Red))
          ),
        ),
      )
    },
//    barProperties = BarProperties(
//      radius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
//      spacing = 3.dp,
//      strokeWidth = 20.dp
//    ),
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow
    ),
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  ComposeChartsTheme {
    PieExample()
  }
}
