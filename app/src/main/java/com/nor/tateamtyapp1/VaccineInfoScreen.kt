@file:OptIn(ExperimentalMaterial3Api::class)

package com.nor.tateamtyapp1

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val infoBgColor = Color(0xFF002D5A)
private val cardBgColor = Color(0xFF004080)
private val accentGold = Color(0xFFFFD700)

data class DiseaseDetail(
    val number: String,
    val name: String,
    val description: String
)

data class VaccineInfoItem(
    val indexNumber: String,
    val title: String,
    val timing: String,
    val protectsAgainst: String,
    val importance: String,
    val diseasesList: List<DiseaseDetail> = emptyList(),
    val emoji: String
)

@Composable
fun VaccineInfoScreen(onBackPressed: () -> Unit) {
    val vaccineList = remember {
        listOf(
            VaccineInfoItem(
                indexNumber = "١",
                title = "لقاح الدرن (BCG)",
                timing = "عند الولادة مباشرة",
                protectsAgainst = "مرض السل (الدرن الرئوي والسحائي)",
                importance = "يحمي الأطفال الحديثي الولادة من الأشكال الحادة والخطيرة لمرض السل الذي يصيب الرئتين والجهاز العصبي.",
                emoji = "💉"
            ),
            VaccineInfoItem(
                indexNumber = "٢",
                title = "لقاح التهاب الكبد البائي (Hep B)",
                timing = "عند الولادة، 2، 4، 6 أشهر",
                protectsAgainst = "فيروس التهاب الكبد B المزمن",
                importance = "يقي الكبد من الالتهابات الفيروسية المزمنة التي قد تؤدي مستقبلاً إلى تلف أو تليف الكبد.",
                emoji = "🛡️"
            ),
            VaccineInfoItem(
                indexNumber = "٣",
                title = "اللقاح الخماسي (Pentavalent)",
                timing = "2، 4، 6 أشهر",
                protectsAgainst = "الدفتيريا، الكزاز، السعال الديكي، التهاب الكبد B، والمستدمية النزلية",
                importance = "لقاح مدمج أساسي يوفر حماية فائقة ضد 5 أمراض خطيرة:",
                diseasesList = listOf(
                    DiseaseDetail("١", "الدفتيريا (الخناق)", "تسبب غشاء في الحلق وانسداد المجاري التنفسية وضيق التنفس."),
                    DiseaseDetail("٢", "الكزاز (التتانوس)", "يسبب تقلصات وتشنجات عضلية مؤلمة وخطيرة جداً."),
                    DiseaseDetail("٣", "السعال الديكي", "يسبب نوبات سعال حادة ومستمرة تؤدي إلى صعوبة التنفس لدى الرضع."),
                    DiseaseDetail("٤", "التهاب الكبد البائي (B)", "عدوى فيروسية تسبب التهاب وتلف الكبد المزمن."),
                    DiseaseDetail("٥", "المستدمية النزلية B (Hib)", "بكتيريا تسبب التهاب السحايا الخطير والتهاب الرئة.")
                ),
                emoji = "🦠"
            ),
            VaccineInfoItem(
                indexNumber = "٤",
                title = "لقاح شلل الأطفال (OPV / IPV)",
                timing = "عند الولادة، 2، 4، 6، 12، 18 شهر",
                protectsAgainst = "مرض شلل الأطفال (Poliomyelitis)",
                importance = "يحمي الجهاز العصبي للطفل ويمنع الإصابة بشلل الأطفال الدائم والعجز الحركي.",
                emoji = "🦵"
            ),
            VaccineInfoItem(
                indexNumber = "٥",
                title = "لقاح فيروس الروتا (Rotavirus)",
                timing = "2، 4، 6 أشهر",
                protectsAgainst = "النزلات المعوية الحادة والإسهال الشديد",
                importance = "يقلل من خطر الإصابة بالجفاف الحاد والنزلات المعوية الشديدة التي تُعد من أكثر أسباب تنويم الرضع بالمستشفيات.",
                emoji = "💧"
            ),
            VaccineInfoItem(
                indexNumber = "٦",
                title = "لقاح المكورات الرئوية (Pneumococcal)",
                timing = "2، 4، 6، 12 شهر",
                protectsAgainst = "التهاب الرئة، التهاب السحايا، والتهاب الأذن الوسطى",
                importance = "يحمي الجهاز التنفسي والأنسجة العصبية من البكتيريا المكورة الرئوية المسببة لالتهاب الرئة الحاد.",
                emoji = "🫁"
            ),
            VaccineInfoItem(
                indexNumber = "٧",
                title = "لقاح الثلاثي الفيروسي (MMR)",
                timing = "12 شهر (سنة) و 18 شهر",
                protectsAgainst = "الحصبة، النكاف، والحصبة الألمانية",
                importance = "يمنع انتشار الفيروسات الثلاثية المعدية ويقي من مضاعفات الحصبة الخطيرة كالحمى العالية والالتهاب الرئوي.",
                emoji = "✨"
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "دليل التطعيمات والتوعية",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrowback),
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = infoBgColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(infoBgColor)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🛡️", fontSize = 52.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "لماذا التطعيمات مهمة لطفلك؟",
                        color = accentGold,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "التطعيمات هي وسيلة آمنة وفعالة لحماية طفلك من الأوبئة والأمراض المعدية الخطيرة. تعمل اللقاحات على تدريب جهاز المناعة ليصبح قادراً على التعرف على الفيروسات والبكتيريا ومكافحتها فوراً.",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title for Vaccines List
            Text(
                text = "قائمة التطعيمات الأساسية والأمراض التي تحمي منها:",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Vaccine Cards
            vaccineList.forEach { item ->
                VaccineDetailCard(item = item)
                Spacer(modifier = Modifier.height(14.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Post-Vaccination Care Tips Card
            PostVaccinationCareCard()

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun VaccineDetailCard(item: VaccineInfoItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Expansion Indicator
                Text(
                    text = if (isExpanded) "▲" else "▼",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                // Number, Title, and Timing
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = item.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF002D5A),
                            textAlign = TextAlign.Right
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFF002D5A), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.indexNumber,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "الموعد: ${item.timing}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Emoji Icon
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFFE3F2FD), shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.emoji, fontSize = 28.sp)
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "المرض المستهدف:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFFD32F2F),
                        textAlign = TextAlign.Right
                    )
                    Text(
                        text = item.protectsAgainst,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "أهمية اللقاح والمعلومات:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF0288D1),
                        textAlign = TextAlign.Right
                    )
                    Text(
                        text = item.importance,
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right,
                        lineHeight = 22.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // If vaccine contains a list of 5 diseases (e.g. Pentavalent)
                    if (item.diseasesList.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            item.diseasesList.forEach { disease ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 10.dp),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(
                                            text = disease.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF002D5A),
                                            textAlign = TextAlign.Right
                                        )
                                        Text(
                                            text = disease.description,
                                            fontSize = 14.sp,
                                            color = Color.DarkGray,
                                            textAlign = TextAlign.Right,
                                            lineHeight = 20.sp
                                        )
                                    }

                                    // Number Badge before the disease name (RTL Right side)
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .background(Color(0xFF005A9C), shape = CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = disease.number,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostVaccinationCareCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "نصائح وإرشادات ما بعد التطعيم 💡",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFE65100),
                    textAlign = TextAlign.Right
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            CareTipItem(number = "١", text = "استخدم كمادات ماء بارد أو فاتر على موضع الحقنة في حال وجود احمرار أو تورم بسيط.")
            CareTipItem(number = "٢", text = "يمكن إعطاء الطفل خافض الحرارة الموصى به من قبل الطبيب أو الممرض حسب الجرعة المحددة.")
            CareTipItem(number = "٣", text = "احرص على إرضاع الطفل أو تقديم السوائل له بانتظام مع إعطائه قسطاً كافياً من الراحة.")
            CareTipItem(number = "٤", text = "راجع المركز الصحي أو الطبيب فوراً إذا استمرت الحرارة العالية لأكثر من 48 ساعة أو ظهرت أعراض غير معتادة.")
        }
    }
}

@Composable
fun CareTipItem(number: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color(0xFF3E2723),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Right,
            lineHeight = 22.sp,
            modifier = Modifier.weight(1f).padding(end = 10.dp)
        )
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(Color(0xFFFFB74D), shape = RoundedCornerShape(13.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
