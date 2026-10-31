package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.trading.TradingLesson
import com.example.domain.trading.TradingMentorRepository
import com.example.ui.theme.ZaraBackground
import com.example.ui.theme.ZaraCardBg
import com.example.ui.theme.ZaraCardBorder
import com.example.ui.theme.ZaraCyan
import com.example.ui.theme.ZaraElectricPurple
import com.example.ui.theme.ZaraGreen
import com.example.ui.theme.ZaraPurple
import com.example.ui.theme.ZaraRed
import com.example.ui.theme.ZaraSurface
import com.example.ui.theme.ZaraSurfaceVariant
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary
import com.example.ui.theme.ZaraTextSecondary
import com.example.ui.viewmodel.VoiceAssistantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradingMentorScreen(
    viewModel: VoiceAssistantViewModel,
    onBackClick: () -> Unit,
    onAskZara: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBengali = uiState.preferredLanguage == "bn"

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = remember(isBengali) {
        if (isBengali) {
            listOf("লেসন ও গাইড", "ক্যালকুলেটর", "কুইজ", "ভয়েস মেন্টর")
        } else {
            listOf("Lessons", "Risk Calculator", "Quiz", "Ask Zara")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZaraBackground)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("trading_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ZaraTextPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Zara Trading Mentor",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, ZaraElectricPurple, CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isBengali) "ট্রেডিং মেন্টর" else "Trading Mentor",
                    color = ZaraTextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isBengali) "ক্রিপ্টো ও ফরেক্স প্রশিক্ষণ" else "Crypto, Forex & Stock Education",
                    color = ZaraCyan,
                    fontSize = 12.sp
                )
            }
        }

        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = ZaraSurface,
            contentColor = ZaraElectricPurple,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = ZaraElectricPurple,
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTabIndex == index) ZaraCyan else ZaraTextSecondary,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (selectedTabIndex) {
                0 -> TradingLessonsTab(isBengali = isBengali, onSpeakLesson = { text ->
                    viewModel.testVoiceSample(text)
                })
                1 -> RiskCalculatorTab(isBengali = isBengali)
                2 -> TradingQuizTab(isBengali = isBengali)
                3 -> AskZaraTradingTab(isBengali = isBengali, onAsk = onAskZara)
            }
        }
    }
}

@Composable
fun TradingLessonsTab(
    isBengali: Boolean,
    onSpeakLesson: (String) -> Unit
) {
    val lessons = TradingMentorRepository.lessons

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(lessons, key = { it.id }) { lesson ->
            var expanded by remember { mutableStateOf(false) }

            Card(
                colors = CardDefaults.cardColors(containerColor = ZaraCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(text = lesson.emoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isBengali) lesson.titleBn else lesson.titleEn,
                                color = ZaraTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Toggle",
                                tint = ZaraTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isBengali) lesson.summaryBn else lesson.summaryEn,
                        color = ZaraTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    AnimatedVisibility(visible = expanded) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ZaraSurfaceVariant)
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = if (isBengali) lesson.fullContentBn else lesson.fullContentEn,
                                    color = ZaraTextPrimary,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Key Takeaway Badge
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ZaraPurple.copy(alpha = 0.2f))
                                    .border(1.dp, ZaraPurple.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💡 ", fontSize = 16.sp)
                                Text(
                                    text = if (isBengali) lesson.keyTakeawayBn else lesson.keyTakeawayEn,
                                    color = ZaraCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Listen to Zara reading this lesson
                            Button(
                                onClick = {
                                    val textToRead = if (isBengali) {
                                        "${lesson.titleBn}। ${lesson.fullContentBn}। মনে রাখবেন: ${lesson.keyTakeawayBn}"
                                    } else {
                                        "${lesson.titleEn}. ${lesson.fullContentEn}. Remember: ${lesson.keyTakeawayEn}"
                                    }
                                    onSpeakLesson(textToRead)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ZaraElectricPurple),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Listen",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBengali) "জারার মুখে লেসনটি শুনুন" else "Listen to Zara explain",
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RiskCalculatorTab(isBengali: Boolean) {
    var capitalInput by remember { mutableStateOf("1000") }
    var riskPercentInput by remember { mutableStateOf("2") }
    var entryPriceInput by remember { mutableStateOf("100") }
    var stopLossInput by remember { mutableStateOf("95") }

    val capital = capitalInput.toDoubleOrNull() ?: 1000.0
    val riskPercent = riskPercentInput.toDoubleOrNull() ?: 2.0
    val entryPrice = entryPriceInput.toDoubleOrNull() ?: 100.0
    val stopLoss = stopLossInput.toDoubleOrNull() ?: 95.0

    val result = remember(capital, riskPercent, entryPrice, stopLoss) {
        TradingMentorRepository.calculateRisk(capital, riskPercent, entryPrice, stopLoss)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = if (isBengali) "রিস্ক ও পজিশন সাইজ ক্যালকুলেটর" else "Position Size & Risk Calculator",
                color = ZaraTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isBengali) "স্টপ-লস ও মূলধন অনুযায়ী সঠিক সাইজ নির্ধারণ করুন যাতে কখনো বড় লস না হয়।" else "Calculate exact position size to never blow up your account.",
                color = ZaraTextSecondary,
                fontSize = 13.sp
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ZaraCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = capitalInput,
                        onValueChange = { capitalInput = it },
                        label = { Text(if (isBengali) "মোট মূলধন / একাউন্ট ব্যালেন্স ($)" else "Account Capital ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZaraElectricPurple,
                            unfocusedBorderColor = ZaraCardBorder,
                            focusedTextColor = ZaraTextPrimary,
                            unfocusedTextColor = ZaraTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = riskPercentInput,
                        onValueChange = { riskPercentInput = it },
                        label = { Text(if (isBengali) "প্রতি ট্রেডে রিস্ক % (প্রস্তাবিত: ১-২%)" else "Risk % Per Trade (Rec: 1-2%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZaraElectricPurple,
                            unfocusedBorderColor = ZaraCardBorder,
                            focusedTextColor = ZaraTextPrimary,
                            unfocusedTextColor = ZaraTextPrimary
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = entryPriceInput,
                            onValueChange = { entryPriceInput = it },
                            label = { Text(if (isBengali) "এন্ট্রি প্রাইস ($)" else "Entry Price ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ZaraElectricPurple,
                                unfocusedBorderColor = ZaraCardBorder,
                                focusedTextColor = ZaraTextPrimary,
                                unfocusedTextColor = ZaraTextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = stopLossInput,
                            onValueChange = { stopLossInput = it },
                            label = { Text(if (isBengali) "স্টপ-লস প্রাইস ($)" else "Stop-Loss ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ZaraElectricPurple,
                                unfocusedBorderColor = ZaraCardBorder,
                                focusedTextColor = ZaraTextPrimary,
                                unfocusedTextColor = ZaraTextPrimary
                            )
                        )
                    }
                }
            }
        }

        // Calculation Results Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ZaraSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ZaraElectricPurple),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isBengali) "হিসাবের ফলাফল:" else "Calculation Results:",
                        color = ZaraCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    ResultRow(
                        label = if (isBengali) "সর্বোচ্চ লস সীমা (Max Risk):" else "Max Loss (Risk $):",
                        value = "$${String.format("%.2f", result.riskAmount)}",
                        highlightColor = ZaraRed
                    )

                    ResultRow(
                        label = if (isBengali) "সঠিক পজিশন সাইজ (Units):" else "Position Size (Units):",
                        value = String.format("%.4f", result.positionSizeUnits),
                        highlightColor = ZaraTextPrimary
                    )

                    ResultRow(
                        label = if (isBengali) "মোট ইনভেস্টমেন্ট সাইজ:" else "Total Trade Value:",
                        value = "$${String.format("%.2f", result.totalInvestment)}",
                        highlightColor = ZaraTextPrimary
                    )

                    ResultRow(
                        label = if (isBengali) "১:২ রিওয়ার্ড টার্গেট প্রাইজ:" else "1:2 Target Take-Profit:",
                        value = "$${String.format("%.2f", result.targetPriceFor1To2)}",
                        highlightColor = ZaraGreen
                    )

                    ResultRow(
                        label = if (isBengali) "সম্ভাব্য লাভ (Target Profit):" else "Target Profit:",
                        value = "+$${String.format("%.2f", result.targetProfit)}",
                        highlightColor = ZaraGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String, highlightColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = ZaraTextSecondary, fontSize = 13.sp)
        Text(text = value, color = highlightColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TradingQuizTab(isBengali: Boolean) {
    val quizzes = TradingMentorRepository.quizzes
    var currentQuizIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var showExplanation by remember { mutableStateOf(false) }

    val quiz = quizzes[currentQuizIndex]

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBengali) "ট্রেডিং প্র্যাকটিস কুইজ" else "Trading Knowledge Quiz",
                    color = ZaraTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${currentQuizIndex + 1}/${quizzes.size}",
                    color = ZaraCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ZaraCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isBengali) quiz.questionBn else quiz.questionEn,
                        color = ZaraTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val options = if (isBengali) quiz.optionsBn else quiz.optionsEn
                    options.forEachIndexed { index, option ->
                        val isSelected = selectedOption == index
                        val isCorrect = index == quiz.correctIndex

                        val borderCol = when {
                            showExplanation && isCorrect -> ZaraGreen
                            showExplanation && isSelected && !isCorrect -> ZaraRed
                            isSelected -> ZaraElectricPurple
                            else -> ZaraCardBorder
                        }

                        val bgCol = when {
                            showExplanation && isCorrect -> ZaraGreen.copy(alpha = 0.15f)
                            showExplanation && isSelected && !isCorrect -> ZaraRed.copy(alpha = 0.15f)
                            isSelected -> ZaraElectricPurple.copy(alpha = 0.2f)
                            else -> ZaraSurfaceVariant
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgCol)
                                .border(1.5.dp, borderCol, RoundedCornerShape(12.dp))
                                .clickable(enabled = !showExplanation) {
                                    selectedOption = index
                                    showExplanation = true
                                }
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "${('A'.code + index).toChar()}. $option",
                                color = ZaraTextPrimary,
                                fontSize = 14.sp
                            )
                        }
                    }

                    if (showExplanation) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ZaraPurple.copy(alpha = 0.2f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = if (isBengali) quiz.explanationBn else quiz.explanationEn,
                                color = ZaraCyan,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                selectedOption = null
                                showExplanation = false
                                currentQuizIndex = (currentQuizIndex + 1) % quizzes.size
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ZaraElectricPurple),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isBengali) "পরবর্তী প্রশ্ন" else "Next Question",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AskZaraTradingTab(
    isBengali: Boolean,
    onAsk: (String) -> Unit
) {
    val suggestedQuestions = remember(isBengali) {
        if (isBengali) {
            listOf(
                "ট্রেডিংয়ে ১-২% রুল কীভাবে কার্যকর করব?",
                "RSI ইন্ডিকেটর দিয়ে কীভাবে বাই-সেল সিগন্যাল পাব?",
                "বুলিশ এনগালফিং এবং হ্যামার ক্যান্ডেলের পার্থক্য কী?",
                "সাপোর্ট ও রেজিস্ট্যান্সে কীভাবে পারফেক্ট এন্ট্রি নেব?",
                "স্পট ও ফিউচার্স ট্রেডিংয়ের মূল পার্থক্য ও ঝুঁকি কী?",
                "ট্রেডিং সাইকোলজি ও ফোমো (FOMO) কীভাবে কন্ট্রোল করব?",
                "বিটকয়েন ট্রেডিংয়ের ফান্ডামেন্টাল নিউজ কীভাবে দেখব?"
            )
        } else {
            listOf(
                "How do I apply the 1-2% risk rule in trading?",
                "How does the RSI indicator give buy/sell signals?",
                "What is the difference between Bullish Engulfing and Hammer?",
                "How to take high-probability entries at Support & Resistance?",
                "What are the major differences and risks of Spot vs Futures?",
                "How do I control emotion, FOMO and revenge trading?",
                "What macroeconomic news drives Bitcoin and Crypto?"
            )
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ZaraCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Zara Mentor",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (isBengali) "জারার সাথে সরাসরি ট্রেডিং আলোচনা" else "Ask Zara Voice Trading Mentor",
                            color = ZaraTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBengali) "যেকোনো ট্রেডিং প্রশ্ন মুখে বলুন বা নিচের টপিক সিলেক্ট করুন।" else "Ask any trading query by voice or tap a suggestion below.",
                            color = ZaraTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = if (isBengali) "জনপ্রিয় ট্রেডিং প্রশ্নসমূহ:" else "Popular Trading Questions:",
                color = ZaraCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(suggestedQuestions) { prompt ->
            Card(
                colors = CardDefaults.cardColors(containerColor = ZaraSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAsk(prompt) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = prompt,
                        color = ZaraTextPrimary,
                        fontSize = 13.5.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Ask",
                        tint = ZaraCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
