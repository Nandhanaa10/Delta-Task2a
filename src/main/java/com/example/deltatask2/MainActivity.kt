package com.example.deltatask2

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.deltatask2.ui.theme.DeltaTask2Theme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.random.Random
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.animation.core.*

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp

data class BulletData(
    val id: String = UUID.randomUUID().toString(),
    val x: Float,
    val y: Float
)
class GameViewModel : ViewModel() {
    var savedPositions by mutableStateOf<List<Pair<Float, Float>>?>(null)
    var gunOffset by mutableStateOf(Offset(0f, 0f))
    var bulletList = mutableStateListOf<BulletData>()
        private set

    fun fireBullet(x: Float, y: Float) {
        bulletList.add(BulletData(x = x, y = y))
    }

    fun removeBullet(bullet: BulletData) {
        bulletList.remove(bullet)
    }
    var isPaused by mutableStateOf(false)
        private set

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }
    var mushroomRects by mutableStateOf<List<androidx.compose.ui.geometry.Rect>>(emptyList())

    fun initializeMushrooms(screenWidth: Float, screenHeight: Float, imageSizePx: Float) {
        if (savedPositions == null) {
            val placed = mutableListOf<androidx.compose.ui.geometry.Rect>()
            fun nonoverlapping(): Offset {
                repeat(500) {
                    val x = Random.nextFloat() * (screenWidth - imageSizePx)
                    val y = Random.nextFloat() * (screenHeight - imageSizePx)
                    val newRect = androidx.compose.ui.geometry.Rect(x, y, x + imageSizePx, y + imageSizePx)
                    if (placed.none { it.overlaps(newRect) }) {
                        placed.add(newRect)
                        return Offset(x, y)
                    }
                }
                return Offset(0f, 0f)
            }
            val newPositions = List(10) { nonoverlapping() }
            savedPositions = newPositions.map { it.x to it.y }
            mushroomRects = placed
        }
    }


}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            changeScreen()
        }
    }
}

@Composable
fun background(controller: NavController,viewModel: GameViewModel){

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(top = 45.dp)

        ){
            Image(
                painter = painterResource(id = R.drawable.downbg),
                contentDescription = "Bottom bg",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = 50f
                    }
            )
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)


            ){
                val density = LocalDensity.current
                val screenwidth = with(density){constraints.maxWidth.toFloat()}
                val screenheight =with(density){constraints.maxHeight.toFloat()}
                val imageSizePx = with(density) { 40.dp.toPx() }
                fun Offset.toSerializablePair(): Pair<Float, Float> = Pair(x, y)
                fun Pair<Float, Float>.toOffset(): Offset = Offset(first, second)

// Save list of positions
                var savedPositions by remember { mutableStateOf(viewModel.savedPositions) }

                val placed = remember { mutableListOf<androidx.compose.ui.geometry.Rect>()}
                LaunchedEffect(Unit) {
                    viewModel.initializeMushrooms(screenwidth, screenheight, imageSizePx)
                }
                val positions = viewModel.savedPositions?.map { Offset(it.first, it.second) } ?: emptyList()

                positions.forEach{pos ->
                    Image(
                        painter = painterResource(R.drawable.mushroom),
                        contentDescription = "mushroom",
                        modifier = Modifier
                            .size(40.dp)
                            .absoluteOffset(
                                x=with(density){pos.x.toDp()},
                                y=with(density){pos.y.toDp()}
                            )

                    )
                }

            }
            Row (
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ){

                    JoystickGunControl(viewModel)

                }
            }



        }
    }
    pausebutton(onClick ={controller.navigate("screen2")
    viewModel.pause()})
}


@Composable
fun JoystickGunControl(viewModel: GameViewModel) {
    var offsetX by remember { mutableStateOf(0f) }
    var stepY by remember { mutableStateOf(1) } // Step 0 (top), 1 (center), 2 (bottom)
    var gunPosition by remember { mutableStateOf(Offset.Zero) }
    val yPositions = listOf(-150f, 0f, 150f)
    var bulletPressCount by remember { mutableIntStateOf(0) }
    val maxPresses = 10
    val isButtonEnabled = bulletPressCount < maxPresses
    var showLostDialog by remember { mutableStateOf(false) }
    val positions = viewModel.savedPositions?.map { Offset(it.first, it.second) } ?: emptyList()
    val gunBaseY = with(LocalDensity.current) { // Calculate base Y in pixels
        (LocalConfiguration.current.screenHeightDp.dp.toPx()) - 100f - 260f
    }
    CaterpillarScreen(viewModel,viewModel.savedPositions?.map { Offset(it.first, it.second) } ?: emptyList())
    val coroutineScope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.gun),
            contentDescription = "Gun",
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    translationY = -260f
                }
                .offset { IntOffset(offsetX.roundToInt(), yPositions[stepY].roundToInt()) }
                .onGloballyPositioned { coordinates ->
                    val positionInRoot = coordinates.positionInRoot()
                    gunPosition = positionInRoot
                }
        )
        Image(
            painter = painterResource(R.drawable.bullet),
            contentDescription = "Bullet",
            modifier = Modifier
                .height(70.dp)
                .width(70.dp)
                .align(Alignment.BottomStart)
                .offset(y = (-110).dp)
                .padding(start = 10.dp)
                .rotate(45f)
                .clickable(enabled = isButtonEnabled) {
                    if (bulletPressCount < maxPresses){
                        bulletPressCount++
                        coroutineScope.launch {
                            delay(500)
                            repeat(5) {
                                viewModel.fireBullet(gunPosition.x , gunPosition.y)
                                delay(500)
                            }
                        }
                        if (bulletPressCount == maxPresses) {
                            showLostDialog = true
                        }
                    }

                }
        )
        if (showLostDialog) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Game Over") },
                text = { Text("You Lost! No more bullets left.") },
                confirmButton = {
                    Button(onClick = {
                        showLostDialog = false
                        // Optionally reset game:
                        // bulletPressCount = 0
                    }) {
                        Text("OK")
                    }
                }
            )
        }

        viewModel.bulletList.forEach { bullet ->
            Bullet(bullet) {
                viewModel.removeBullet(bullet)
            }
        }

        // Joystick
        Joystick(
            onMove = { dx, dy ->
                offsetX = dx * 2f // scale movement (tune as needed)
                stepY = when {
                    dy < -50 -> 0 // Up
                    dy > 50 -> 2  // Down
                    else -> 1
                }},
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
                .graphicsLayer {
                    translationX = 50f
                    translationY = 15f
                }
        )
    }
}

@Composable
fun Bullet(
    bullet: BulletData,
    onEnd: () -> Unit
) {
    val bulletY = remember { Animatable(bullet.y) }

    LaunchedEffect(Unit) {
        bulletY.animateTo(
            targetValue = -1000f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
        onEnd()
    }

    Image(
        painter = painterResource(id = R.drawable.bulletfiring),
        contentDescription = "Bullet",
        modifier = Modifier
            .size(32.dp)
            .offset {
                IntOffset(bullet.x.roundToInt(), bulletY.value.roundToInt())
            }
    )
}

@Composable
fun Joystick(
    modifier: Modifier = Modifier,
    onMove: (Float, Float) -> Unit
) {
    val radius = 100f
    var thumbOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .size(100.dp)
            .graphicsLayer {
                translationY = -200f
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        thumbOffset = Offset.Zero
                        onMove(0f, 0f)
                    }
                ) { change, dragAmount ->
                    val newOffset = thumbOffset + dragAmount
                    val clampedOffset = if (newOffset.getDistance() <= radius) {
                        newOffset
                    } else {
                        val angle = atan2(newOffset.y, newOffset.x)
                        Offset(
                            x = cos(angle) * radius,
                            y = sin(angle) * radius
                        )
                    }
                    thumbOffset = clampedOffset
                    onMove(clampedOffset.x, clampedOffset.y)
                    change.consume()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color.Gray, shape = CircleShape)
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffset.x.roundToInt(), thumbOffset.y.roundToInt()) }
                .size(50.dp)
                .background(Color.DarkGray, shape = CircleShape)
        )
    }
}

@Composable
fun pausebutton(onClick: () -> Unit){ //unit is similar to void
    Image(
        painter = painterResource(id = R.drawable.pause),
        contentDescription = "Pause button",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(64.dp)
            .clickable { onClick() }
            .height(20.dp)
            .width(20.dp)
            .padding(all = 10.dp)
            .clip(RoundedCornerShape(12.dp))

    )
}


@Composable
fun changeScreen(){
    val controller = rememberNavController()
    val gameViewModel: GameViewModel = viewModel()
    NavHost(navController = controller, startDestination = "screen1"){
        composable("screen1"){background(controller, gameViewModel)}
        composable("screen2"){ScreenTwo(controller, gameViewModel)}
    }
}

@Composable
fun ScreenTwo(controller:NavController,viewModel: GameViewModel){
    Box(
        modifier = Modifier.fillMaxSize()

            .background(color = Color.Black.copy(alpha = 0f)),
        contentAlignment = Alignment.Center
    ){
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.5f) // semi-transparent background
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)

        ){
            Row(
                horizontalArrangement = Arrangement.Center, // ✅ center horizontally
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(all = 16.dp)
                    .fillMaxSize()
            ) {
                resumebutton ( onClick = {
                    controller.popBackStack()
                    viewModel.resume()} )
            }
        }
    }
}


@Composable
fun resumebutton(
    onClick: () -> Unit){
    Image(
        painter = painterResource(id = R.drawable.resume),
        contentDescription = "Resume button",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(120.dp)
            .clickable { onClick() }
            .padding(all = 10.dp)
            .clip(RoundedCornerShape(12.dp))
    )
}

@Composable
fun CaterpillarScreen(gameViewModel: GameViewModel,positions:List<Offset>) {
    val isPaused = gameViewModel.isPaused
    val mushroomSizePx = with(LocalDensity.current) { 40.dp.toPx() }

    Box(modifier = Modifier.fillMaxSize()) {
        Caterpillar(
            isPaused = isPaused,
            positions = positions,
        )
    }
}


@Composable
fun Caterpillar(
    segmentCount: Int = 6,
    segmentSpacing: Int = 0,
    segmentRadius: Float = 20f,
    speed: Float = 200f,
    stepDownDp: Dp = 20.dp,
    isPaused: Boolean,
    positions: List<Offset>
) {
    val stepDownPx = with(LocalDensity.current) { stepDownDp.toPx() }
    val mushroomSizePx = with(LocalDensity.current) { 40.dp.toPx() }

    val offsetX = remember { Animatable(0f) }
    var goingRight by remember { mutableStateOf(true) }
    var verticalOffset by remember { mutableStateOf(0f) }

    var isBending by remember { mutableStateOf(false) }
    val animatedBendAngle by animateFloatAsState(
        targetValue = if (isBending) -30f else 0f,
        animationSpec = tween(300),
        label = "bend"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val maxWidth = constraints.maxWidth.toFloat()
        val segmentLength = segmentRadius * 2 + segmentSpacing

        LaunchedEffect(isPaused) {
            val frameDelay = 16L
            while (true) {
                if (!isPaused) {
                    val delta = (speed * frameDelay) / 1000f
                    val headX = if (goingRight) offsetX.value else offsetX.value - (segmentCount - 1) * segmentLength
                    val headCenterY = verticalOffset + segmentRadius * 2 + animatedBendAngle

                    // Head is drawn centered, so topLeft is offset - radius
                    val caterpillarHeadRect = androidx.compose.ui.geometry.Rect(
                        offset = Offset(headX - segmentRadius, headCenterY - segmentRadius),
                        size = Size(segmentRadius * 2, segmentRadius * 2)
                    )

                    val hitMushroom = positions.any { mushroomPos ->
                        val mushroomRect = androidx.compose.ui.geometry.Rect(
                            offset = mushroomPos,
                            size = Size(mushroomSizePx, mushroomSizePx)
                        )
                        mushroomRect.overlaps(caterpillarHeadRect)
                    }

                    if (hitMushroom && !isBending) {
                        isBending = true
                        goingRight = !goingRight

                        val targetY = verticalOffset + stepDownPx
                        animate(
                            initialValue = verticalOffset,
                            targetValue = targetY,
                            animationSpec = tween(300)
                        ) { value, _ ->
                            verticalOffset = value
                        }

                        delay(300)
                        isBending = false
                    }

                    val nextHeadX = if (goingRight) headX + delta else headX - delta
                    val willHitRight = goingRight && nextHeadX + segmentRadius >= maxWidth
                    val willHitLeft = !goingRight && nextHeadX - segmentRadius <= 0

                    if (willHitRight || willHitLeft) {
                        goingRight = !goingRight
                        verticalOffset += stepDownPx
                    } else {
                        offsetX.snapTo(offsetX.value + if (goingRight) delta else -delta)
                    }
                }
                delay(frameDelay)
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            // 🍄 Draw mushroom hit boxes
            positions.forEach { mushroomPos ->
                drawRect(
                    color = Color.Red,
                    topLeft = mushroomPos,
                    size = Size(mushroomSizePx, mushroomSizePx),
                    style = Stroke(1f)
                )
            }

            for (i in 0 until segmentCount) {
                val segmentIndex = if (goingRight) i else segmentCount - 1 - i
                val x = offsetX.value - segmentIndex * segmentLength * if (goingRight) 1 else -1
                if (x < -segmentRadius || x > size.width + segmentRadius) continue

                val bendFactor = 1f - (i.toFloat() / segmentCount.toFloat())
                val centerY = verticalOffset + segmentRadius * 2 + (animatedBendAngle * bendFactor)
                val center = Offset(x, centerY)

                drawCircle(color = Color.Green, radius = segmentRadius, center = center)
                drawCircle(color = Color.Black, radius = segmentRadius, center = center, style = Stroke(4f))

                // 🟦 Draw caterpillar head bounding box (for debug)
                if ((goingRight && i == 0) || (!goingRight && i == segmentCount - 1)) {


                    // 👀 Eyes
                    val eyeOffsetX = segmentRadius / 2.5f
                    val eyeOffsetY = segmentRadius / 2.5f
                    val eyeRadius = segmentRadius / 6
                    drawCircle(color = Color.Black, radius = eyeRadius, center = Offset(center.x - eyeOffsetX, center.y - eyeOffsetY))
                    drawCircle(color = Color.Black, radius = eyeRadius, center = Offset(center.x + eyeOffsetX, center.y - eyeOffsetY))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DeltaTask2Theme {
        changeScreen()


    }
}