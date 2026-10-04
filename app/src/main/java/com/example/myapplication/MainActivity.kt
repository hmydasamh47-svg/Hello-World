package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.geometry.Size
import androidx.compose.animation.AnimatedVisibility
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.res.painterResource
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

// ==========================================
// التنقل بين الشاشات (Navigation Enum)
// ==========================================
enum class Screen {
    HOME,
    MINIGAMES,
    FIND_PAIRS,
    BUG_SNAKE,
    MEMORY
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            Screen.HOME -> HomeScreen(onNavigateToMinigames = { currentScreen = Screen.MINIGAMES })
            Screen.MINIGAMES -> MinigamesScreen(
                onBack = { currentScreen = Screen.HOME },
                onSelectGame = { game ->
                    when (game) {
                        "pairs" -> currentScreen = Screen.FIND_PAIRS
                        "snake" -> currentScreen = Screen.BUG_SNAKE
                        "memory" -> currentScreen = Screen.MEMORY
                    }
                }
            )
            Screen.FIND_PAIRS -> FindPairsGameScreen(onBack = { currentScreen = Screen.MINIGAMES })
            Screen.BUG_SNAKE -> BugSnakeGameScreen(onBack = { currentScreen = Screen.MINIGAMES })
            Screen.MEMORY -> MemoryGameScreen(onBack = { currentScreen = Screen.MINIGAMES })
        }
    }
}

// ==========================================
// 1. الشاشة الرئيسية (Home Screen)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToMinigames: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hello World", color = Color.LightGray, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E517B))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF9F4F1))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                InfoCard(
                    title = "What is \"Hello World!\"?",
                    content = "A \"Hello World!\" is a computer program that displays the message \"Hello World!\". It is commonly used to introduce programming languages; it is considered one of the most typical exercises when starting in the programming world."
                )
            }

            item {
                InfoCard(
                    title = "Purpose",
                    content = "A \"Hello World!\" is commonly used to introduce beginning programmers to a programming language. \"Hello World!\" is also used as a test to ensure that the programming language to be used is correctly installed on the computer."
                )
            }

            item {
                InfoCard(
                    title = "History",
                    content = "It is true that there is a variety of test programs from the development of programmable computers, the tradition of using the phrase \"Hello World!\" as a test message that was influenced by an example program illustrated in the book \"The C Programming Language\", where the sample Program Hello World is shown (without quotes), and was inherited from a 1974 Bell Laboratories internal memorandum by Brian Kernighan, \"Programming in C: A Tutorial\"."
                )
            }

            item {
                CodeExampleCard(
                    title = "Example in C",
                    code = "#include <stdio.h>\n\nint main() {\n    printf(\"Hello World!\");\n    return 0;\n}"
                )
            }

            item {
                InfoCard(
                    title = "Output",
                    content = "Hello World!"
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToMinigames() },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Minigames", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB85D1A))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Try a mini game, they are all themed around the art of programming.",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("💻 🖥️", fontSize = 40.sp)
                    }
                }
            }

            item {
                InfoCard(
                    title = "What is a \"bug\"?",
                    content = "In 1978, Thomas Alva Edison mentioned that one of his apparatus stopped working because a bug was inside it, so since the birth of computing, the term \"bug\" was adopted to refer to an error or failure in a computer program or hardware system."
                )
            }

            item {
                InfoCard(
                    title = "More examples",
                    content = "To see more examples visit:\nhttp://helloworldcollection.de/"
                )
            }
        }
    }
}

// ==========================================
// 2. شاشة قائمة الألعاب (Minigames Screen)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinigamesScreen(onBack: () -> Unit, onSelectGame: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minigames", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E517B))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF9F9F9))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GameItemCard(
                iconText = "🎴",
                title = "Find the pairs",
                description = "Find all programming language pairs and beat your own record.",
                onClick = { onSelectGame("pairs") }
            )
            GameItemCard(
                iconText = "🐞",
                title = "BugSnake",
                description = "Get rid of all the bugs you find along the way and beat your own record.",
                onClick = { onSelectGame("snake") }
            )
            GameItemCard(
                iconText = "🧠",
                title = "Memory",
                description = "Memorize all possible programming language icon patterns and beat your own record.",
                onClick = { onSelectGame("memory") }
            )
        }
    }
}

// ==========================================
// 3. لعبة مطابقة الأزواج (Find the Pairs)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindPairsGameScreen(onBack: () -> Unit) {
    val itemsList = remember {
        listOf("JS", "C++", "🤖", "🐚", "⚙️", "🐍", "☕", "🚀", "💎", "🎯")
            .let { (it + it).shuffled() }
    }

    var revealedIndices by remember { mutableStateOf(setOf<Int>()) }
    var matchedIndices by remember { mutableStateOf(setOf<Int>()) }
    var selectedIndices by remember { mutableStateOf(listOf<Int>()) }
    var attempts by remember { mutableIntStateOf(0) }
    var bestRecord by remember { mutableIntStateOf(57) }

    fun resetGame() {
        revealedIndices = emptySet()
        matchedIndices = emptySet()
        selectedIndices = emptyList()
        attempts = 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find the pairs", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { resetGame() }) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E517B))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF537895))
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(20) { index ->
                        val isRevealed = index in revealedIndices || index in matchedIndices
                        Card(
                            modifier = Modifier
                                .aspectRatio(0.85f)
                                .clickable(enabled = !isRevealed && selectedIndices.size < 2) {
                                    val newSelected = selectedIndices + index
                                    selectedIndices = newSelected
                                    revealedIndices = revealedIndices + index

                                    if (newSelected.size == 2) {
                                        attempts++
                                        if (itemsList[newSelected[0]] == itemsList[newSelected[1]]) {
                                            matchedIndices = matchedIndices + newSelected
                                            selectedIndices = emptyList()
                                        } else {
                                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                                revealedIndices = revealedIndices - newSelected.toSet()
                                                selectedIndices = emptyList()
                                            }, 800)
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isRevealed) Color(0xFF3B5E7E) else Color(0xFF4A6E8D)
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(1.dp, Color(0xFF6B8EA8), RoundedCornerShape(2.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isRevealed) itemsList[index] else "{ }",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E517B))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🏆", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Number of attempts: $attempts", color = Color.White, fontSize = 14.sp)
                    Text("Best record: $bestRecord", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}






enum class SnakeDirection { UP, DOWN, LEFT, RIGHT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BugSnakeGameScreen(onBack: () -> Unit) {
    var score by remember { mutableIntStateOf(0) }
    var baseSpeed by remember { mutableIntStateOf(3) }
    var currentSpeedDisplay by remember { mutableIntStateOf(3) }
    var bestRecord by remember { mutableIntStateOf(8) }
    var showGameOverDialog by remember { mutableStateOf(false) }

    var showGrid by remember { mutableStateOf(true) }
    var isSnailMode by remember { mutableStateOf(false) }
    var isTurboMode by remember { mutableStateOf(false) }

    val gridWidth = 10
    val gridHeight = 15

    var snake by remember { mutableStateOf(listOf(Pair(5, 5), Pair(4, 5), Pair(3, 5), Pair(2, 5))) }
    var direction by remember { mutableStateOf(SnakeDirection.RIGHT) }
    var bug by remember { mutableStateOf(Pair(2, 2)) }
    var isGameOver by remember { mutableStateOf(false) }

    // مشغل نغمات أصوات النظام المباشر
    val toneGenerator = remember {
        try { ToneGenerator(AudioManager.STREAM_MUSIC, 100) } catch (e: Exception) { null }
    }

    // 1. صوت تغيير الاتجاه عند السحب
    fun playMoveSound() {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
    }

    // 2. صوت أكل الحشرة
    fun playEatSound() {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 80)
    }

    // 3. صوت الخسارة
    fun playGameOverSound() {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 250)

    }
    fun playClickSound() {
        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_S, 50)
    }





    fun restartGame() {
        snake = listOf(Pair(5, 5), Pair(4, 5), Pair(3, 5), Pair(2, 5))
        direction = SnakeDirection.RIGHT
        bug = Pair(Random.nextInt(0, gridWidth), Random.nextInt(0, gridHeight))
        score = 0
        baseSpeed = 3
        isGameOver = false
        showGameOverDialog = false
    }

    val effectiveDelay = when {
        isSnailMode -> 600L
        isTurboMode -> 100L
        else -> (350 - (baseSpeed * 25L)).coerceAtLeast(80L)
    }

    LaunchedEffect(isSnailMode, isTurboMode, baseSpeed) {
        currentSpeedDisplay = when {
            isSnailMode -> 1
            isTurboMode -> 8
            else -> baseSpeed
        }
    }

    LaunchedEffect(isGameOver, effectiveDelay) {
        while (!isGameOver) {
            delay(effectiveDelay)
            val head = snake.first()
            val newHead = when (direction) {
                SnakeDirection.UP -> Pair(head.first, head.second - 1)
                SnakeDirection.DOWN -> Pair(head.first, head.second + 1)
                SnakeDirection.LEFT -> Pair(head.first - 1, head.second)
                SnakeDirection.RIGHT -> Pair(head.first + 1, head.second)
            }

            if (newHead.first < 0 || newHead.first >= gridWidth || newHead.second < 0 || newHead.second >= gridHeight || newHead in snake) {
                isGameOver = true
                playGameOverSound()
                showGameOverDialog = true
                if (score > bestRecord) bestRecord = score
            } else {
                val newSnake = mutableListOf(newHead)
                if (newHead == bug) {
                    score++
                    playEatSound()
                    if (score % 2 == 0 && baseSpeed < 7) baseSpeed++
                    newSnake.addAll(snake)
                    bug = Pair(Random.nextInt(0, gridWidth), Random.nextInt(0, gridHeight))
                } else {
                    newSnake.addAll(snake.dropLast(1))
                }
                snake = newSnake
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BugSnake", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isSnailMode = !isSnailMode
                        if (isSnailMode) isTurboMode = false
                    }) {
                        Text("🐌", fontSize = 18.sp)
                    }

                    IconButton(onClick = {
                        isTurboMode = !isTurboMode
                        if (isTurboMode) isSnailMode = false
                    }) {
                        Text("⚡", fontSize = 18.sp)
                    }

                    IconButton(onClick = { showGrid = !showGrid }) {
                        Text(if (showGrid) "▦" else "▢", fontSize = 20.sp, color = Color.White)
                    }

                    IconButton(onClick = { restartGame() }) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E517B))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF537895))
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val (x, y) = dragAmount
                            val oldDir = direction
                            if (kotlin.math.abs(x) > kotlin.math.abs(y)) {
                                if (x > 0 && direction != SnakeDirection.LEFT) direction = SnakeDirection.RIGHT
                                else if (x < 0 && direction != SnakeDirection.RIGHT) direction = SnakeDirection.LEFT
                            } else {
                                if (y > 0 && direction != SnakeDirection.UP) direction = SnakeDirection.DOWN
                                else if (y < 0 && direction != SnakeDirection.DOWN) direction = SnakeDirection.UP
                            }
                            if (oldDir != direction) {
                                playClickSound() // 🔔 صوت عند تغيير اتجاه الحركة
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cellWidth = size.width / gridWidth
                    val cellHeight = size.height / gridHeight

                    // رسم الشبكة
                    if (showGrid) {
                        for (i in 0..gridWidth) {
                            drawLine(Color(0xFF638AAA), Offset(i * cellWidth, 0f), Offset(i * cellWidth, size.height), strokeWidth = 1f)
                        }
                        for (j in 0..gridHeight) {
                            drawLine(Color(0xFF638AAA), Offset(0f, j * cellHeight), Offset(size.width, j * cellHeight), strokeWidth = 1f)
                        }
                    }

                    // رسم الحشرة
                    val bugCenterX = (bug.first + 0.5f) * cellWidth
                    val bugCenterY = (bug.second + 0.5f) * cellHeight
                    val bugR = cellWidth * 0.28f

                    drawCircle(color = Color.White, radius = bugR * 0.4f, center = Offset(bugCenterX, bugCenterY - bugR * 0.9f))
                    drawCircle(color = Color.White, radius = bugR, center = Offset(bugCenterX, bugCenterY))

                    drawLine(Color.White, Offset(bugCenterX - bugR * 1.5f, bugCenterY - bugR * 0.4f), Offset(bugCenterX + bugR * 1.5f, bugCenterY - bugR * 0.4f), strokeWidth = 2.5f)
                    drawLine(Color.White, Offset(bugCenterX - bugR * 1.6f, bugCenterY + bugR * 0.1f), Offset(bugCenterX + bugR * 1.6f, bugCenterY + bugR * 0.1f), strokeWidth = 2.5f)
                    drawLine(Color.White, Offset(bugCenterX - bugR * 1.4f, bugCenterY + bugR * 0.6f), Offset(bugCenterX + bugR * 1.4f, bugCenterY + bugR * 0.6f), strokeWidth = 2.5f)

                    // رسم الثعبان ورأس الأندرويد القبة
                    snake.forEachIndexed { index, part ->
                        val centerX = (part.first + 0.5f) * cellWidth
                        val centerY = (part.second + 0.5f) * cellHeight

                        if (index == 0) {
                            val headR = cellWidth * 0.35f
                            val startAngle = when (direction) {
                                SnakeDirection.UP -> 180f
                                SnakeDirection.RIGHT -> 270f
                                SnakeDirection.DOWN -> 0f
                                SnakeDirection.LEFT -> 90f
                            }

                            drawArc(
                                color = Color.White,
                                startAngle = startAngle,
                                sweepAngle = 180f,
                                useCenter = true,
                                topLeft = Offset(centerX - headR, centerY - headR),
                                size = Size(headR * 2, headR * 2)
                            )

                            val eyeDist = headR * 0.35f
                            val eyeR = headR * 0.14f
                            when (direction) {
                                SnakeDirection.UP -> {
                                    drawCircle(Color(0xFF537895), eyeR, Offset(centerX - eyeDist, centerY - headR * 0.3f))
                                    drawCircle(Color(0xFF537895), eyeR, Offset(centerX + eyeDist, centerY - headR * 0.3f))
                                    drawLine(Color.White, Offset(centerX - eyeDist, centerY - headR * 0.5f), Offset(centerX - headR * 0.7f, centerY - headR * 1.1f), strokeWidth = 3f)
                                    drawLine(Color.White, Offset(centerX + eyeDist, centerY - headR * 0.5f), Offset(centerX + headR * 0.7f, centerY - headR * 1.1f), strokeWidth = 3f)
                                }
                                SnakeDirection.DOWN -> {
                                    drawCircle(Color(0xFF537895), eyeR, Offset(centerX - eyeDist, centerY + headR * 0.3f))
                                    drawCircle(Color(0xFF537895), eyeR, Offset(centerX + eyeDist, centerY + headR * 0.3f))
                                    drawLine(Color.White, Offset(centerX - eyeDist, centerY + headR * 0.5f), Offset(centerX - headR * 0.7f, centerY + headR * 1.1f), strokeWidth = 3f)
                                    drawLine(Color.White, Offset(centerX + eyeDist, centerY + headR * 0.5f), Offset(centerX + headR * 0.7f, centerY + headR * 1.1f), strokeWidth = 3f)
                                }
                                SnakeDirection.RIGHT -> {
                                    drawCircle(Color(0xFF537895), eyeR, Offset(centerX + headR * 0.3f, centerY - eyeDist))
                                    drawCircle(Color(0xFF537895), eyeR, Offset(centerX + headR * 0.3f, centerY + eyeDist))
                                    drawLine(Color.White, Offset(centerX + headR * 0.5f, centerY - eyeDist), Offset(centerX + headR * 1.1f, centerY - headR * 0.7f), strokeWidth = 3f)
                                    drawLine(Color.White, Offset(centerX + headR * 0.5f, centerY + eyeDist), Offset(centerX + headR * 1.1f, centerY + headR * 0.7f), strokeWidth = 3f)
                                }
                                SnakeDirection.LEFT -> {
                                    drawCircle(Color(0xFF537895), eyeR, Offset(centerX - headR * 0.3f, centerY - eyeDist))
                                    drawCircle(Color(0xFF537895), eyeR, Offset(centerX - headR * 0.3f, centerY + eyeDist))
                                    drawLine(Color.White, Offset(centerX - headR * 0.5f, centerY - eyeDist), Offset(centerX - headR * 1.1f, centerY - headR * 0.7f), strokeWidth = 3f)
                                    drawLine(Color.White, Offset(centerX - headR * 0.5f, centerY + eyeDist), Offset(centerX - headR * 1.1f, centerY + headR * 0.7f), strokeWidth = 3f)
                                }
                            }
                        } else {
                            drawCircle(
                                color = Color.White,
                                radius = cellWidth * 0.33f,
                                center = Offset(centerX, centerY)
                            )
                        }
                    }
                }
            }

            // الشريط السفلي للنتيجة
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E517B))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏆", fontSize = 30.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Score: $score", color = Color.White, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                        Text("Best record: $bestRecord", color = Color.White, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                    }
                }
                Text("Speed: $currentSpeedDisplay", color = Color.White, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
            }
        }

        // نافذة Game Over
        if (showGameOverDialog) {
            AlertDialog(
                onDismissRequest = { showGameOverDialog = false },
                containerColor = Color(0xFF2A5173),
                title = {
                    Text("Hello World", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💻 🖥️", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Game over, do you want to start over?",
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { restartGame() }) {
                        Text("YES", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showGameOverDialog = false
                        onBack()
                    }) {
                        Text("NO", color = Color.White)
                    }
                }
            )
        }
    }
}
// ==========================================
// 5. لعبة الذاكرة (Memory Game Screen)
// ==========================================

data class MemoryItem(
    val id: Int,
    val name: String,
    val iconRes: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryGameScreen(
    onBack: () -> Unit = {}
) {
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }

    DisposableEffect(Unit) {
        onDispose { toneGenerator.release() }
    }

    val allItems = remember {
        listOf(
            MemoryItem(1, "C"),
            MemoryItem(2, "Java"),
            MemoryItem(3, "Python"),
            MemoryItem(4, "R"),
            MemoryItem(5, "C++"),
            MemoryItem(6, "C#"),
            MemoryItem(7, "TS"),
            MemoryItem(8, "Swift"),
            MemoryItem(9, "JS"),
            MemoryItem(10, "PHP"),
            MemoryItem(11, "Rust")
        )
    }

    var score by remember { mutableStateOf(0) }
    var bestRecord by remember { mutableStateOf(2) }

    val cardCount = if (score >= 4) 6 else 4
    val initialTime = if (score >= 4) 9 else 6

    var targetSequence by remember { mutableStateOf<List<MemoryItem>>(emptyList()) }
    var displayOptions by remember { mutableStateOf<List<MemoryItem>>(emptyList()) }
    var userAnswers by remember { mutableStateOf<List<MemoryItem?>>(emptyList()) }
    var wrongAnswerIndex by remember { mutableStateOf<Int?>(null) }

    var timerSeconds by remember { mutableStateOf(initialTime) }
    var isMemorizePhase by remember { mutableStateOf(true) }
    var isGameOver by remember { mutableStateOf(false) }
    var isNewRecordDialog by remember { mutableStateOf(false) }
    var showSuccessToast by remember { mutableStateOf(false) }

    fun startNewRound() {
        val selected = allItems.shuffled().take(cardCount)
        targetSequence = selected
        displayOptions = selected
        userAnswers = List(cardCount) { null }
        wrongAnswerIndex = null
        timerSeconds = if (score >= 4) 9 else 6
        isMemorizePhase = true
        showSuccessToast = false
    }

    LaunchedEffect(score) {
        startNewRound()
    }

    LaunchedEffect(isMemorizePhase, timerSeconds) {
        if (isMemorizePhase && timerSeconds > 0) {
            delay(1000L)
            timerSeconds--
        } else if (isMemorizePhase && timerSeconds == 0) {
            isMemorizePhase = false
            displayOptions = displayOptions.shuffled()
        }
    }

    fun onOptionClicked(item: MemoryItem) {
        if (isMemorizePhase || isGameOver || showSuccessToast || isNewRecordDialog) return

        val currentIndex = userAnswers.indexOfFirst { it == null }
        if (currentIndex != -1) {
            if (targetSequence[currentIndex].id == item.id) {
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 100)
                val newAnswers = userAnswers.toMutableList()
                newAnswers[currentIndex] = item
                userAnswers = newAnswers

                if (!newAnswers.contains(null)) {
                    score++
                    if (score > bestRecord) {
                        bestRecord = score
                        isNewRecordDialog = true
                    } else {
                        showSuccessToast = true
                    }
                    toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 200)
                }
            } else {
                wrongAnswerIndex = currentIndex
                val newAnswers = userAnswers.toMutableList()
                newAnswers[currentIndex] = item
                userAnswers = newAnswers
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300)
                isGameOver = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Memory",
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { startNewRound() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B4965))
            )
        },
        bottomBar = {
            Surface(
                color = Color(0xFF1B4965),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏆 Score: $score",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Best record: $bestRecord",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF3C6E8C))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isMemorizePhase) "Memorize the order of the following\nicons:"
                    else "Select each icon in the order it was initially\ndisplayed:",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val columns = if (cardCount == 6) 3 else 2
                val cardWidth = if (cardCount == 6) 100.dp else 140.dp
                val cardHeight = if (cardCount == 6) 80.dp else 95.dp

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val rows = cardCount / columns
                    for (r in 0 until rows) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            for (c in 0 until columns) {
                                val index = r * columns + c
                                PixelCard(
                                    item = displayOptions.getOrNull(index),
                                    width = cardWidth,
                                    height = cardHeight,
                                    onClick = { displayOptions.getOrNull(index)?.let { onOptionClicked(it) } }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isMemorizePhase) {
                    Text(
                        text = "Time: ${timerSeconds}s.",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp
                    )
                } else {
                    Text(
                        text = "Let's go!",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Your answer:",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val rows = cardCount / columns
                    for (r in 0 until rows) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            for (c in 0 until columns) {
                                val index = r * columns + c
                                PixelAnswerCard(
                                    item = userAnswers.getOrNull(index),
                                    isWrong = wrongAnswerIndex == index,
                                    width = cardWidth,
                                    height = cardHeight
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showSuccessToast,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎉 Good keep it up!",
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = { startNewRound() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Next", fontFamily = FontFamily.Monospace, color = Color.White)
                        }
                    }
                }
            }

            if (isNewRecordDialog) {
                AlertDialog(
                    onDismissRequest = {},
                    containerColor = Color(0xFF1B4965),
                    title = {
                        Text(
                            "Hello World",
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🏆", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Congratulations! You have broken the record, do you want to play again?",
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            isNewRecordDialog = false
                            startNewRound()
                        }) {
                            Text("YES", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            isNewRecordDialog = false
                            onBack()
                        }) {
                            Text("NO", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            if (isGameOver) {
                AlertDialog(
                    onDismissRequest = {},
                    containerColor = Color(0xFF1B4965),
                    title = {
                        Text(
                            "Hello World",
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            "Game over, do you want to start over?",
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            score = 0
                            isGameOver = false
                            startNewRound()
                        }) {
                            Text("YES", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            isGameOver = false
                            onBack()
                        }) {
                            Text("NO", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PixelCard(
    item: MemoryItem?,
    width: Dp,
    height: Dp,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(width, height)
            .clickable { onClick() }
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C5E7A)),
        shape = RoundedCornerShape(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (item != null) {
                if (item.iconRes != null) {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.name,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                } else {
                    Text(
                        text = item.name,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PixelAnswerCard(
    item: MemoryItem?,
    isWrong: Boolean,
    width: Dp,
    height: Dp
) {
    val bgColor = when {
        isWrong -> Color(0xFFB71C1C)
        item != null -> Color(0xFF558B2F)
        else -> Color(0xFF2C5E7A)
    }

    Card(
        modifier = Modifier
            .size(width, height)
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (item != null) {
                if (item.iconRes != null) {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.name,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                } else {
                    Text(
                        text = item.name,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = "{ }",
                    color = Color.White.copy(alpha = 0.8f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 20.sp
                )
            }
        }
    }
}
// ==========================================
// 6. المكونات الرسومية المساعدة (UI Components)
// ==========================================
@Composable
fun InfoCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E517B))
            Spacer(modifier = Modifier.height(8.dp))
            Text(content, fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun CodeExampleCard(title: String, code: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E517B))
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF282C34), shape = RoundedCornerShape(4.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = code,
                    color = Color(0xFFABB2BF),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun GameItemCard(iconText: String, title: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF1E517B), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(iconText, fontSize = 26.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E517B))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                description,
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MemorySquareButton(
    text: String,
    isSuccess: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSuccess) Color(0xFF8BC34A) else Color(0xFF3D6182)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color(0xFF6B8EA8), RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
fun AnswerSlot(text: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(65.dp),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3D6182))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color(0xFF6B8EA8), RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}


