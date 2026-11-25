package com.kora.challenge.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kora.challenge.R
import com.kora.challenge.data.Art
import com.kora.challenge.data.Game
import com.kora.challenge.data.Games
import com.kora.challenge.data.KoraStore
import com.kora.challenge.ui.common.KIcon
import com.kora.challenge.ui.theme.Gold
import com.kora.challenge.ui.theme.LocalKoraColors
import kotlinx.coroutines.launch
import kotlin.math.hypot

// أرقام عناصر الـ LazyColumn (للتمرير من الأزرار زي روابط # في الموقع)
private const val IDX_STATS = 2
private const val IDX_GAMES = 3

@Composable
fun HomeScreen(dark: Boolean, onToggleTheme: () -> Unit, onOpenGame: (String) -> Unit) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dailyDone = remember { KoraStore.loadDaily(KoraStore.todayKey()).done }
    val rows = remember { Games.all.chunked(2) }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()
    ) {
        item { TopBar(dark, onToggleTheme) }
        item {
            Hero(
                onStart = { scope.launch { listState.animateScrollToItem(IDX_GAMES) } },
                onStats = { scope.launch { listState.animateScrollToItem(IDX_STATS) } }
            )
        }
        item { StatsStrip { onOpenGame("leaderboard") } }
        item { SectionHeading() }
        item { DailyBanner(Games.daily, dailyDone) { onOpenGame(Games.daily.id) } }
        items(rows.size) { i ->
            val row = rows[i]
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 10.dp).height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { g -> GameCard(g, Modifier.weight(1f).fillMaxHeight()) { onOpenGame(g.id) } }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        item { Spacer(Modifier.height(28.dp)) }
        item { ChallengeBanner { scope.launch { listState.animateScrollToItem(IDX_GAMES) } } }
        item { Footer() }
        item { Spacer(Modifier.navigationBarsPadding()) }
    }
}

/* ---------------- الهيدر ---------------- */
@Composable
private fun TopBar(dark: Boolean, onToggleTheme: () -> Unit) {
    val k = LocalKoraColors.current
    Column {
        Row(
            Modifier.fillMaxWidth().height(65.dp).padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Logo()
            Box(
                Modifier.size(39.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, k.border, RoundedCornerShape(12.dp)).clickable(onClick = onToggleTheme),
                contentAlignment = Alignment.Center
            ) {
                KIcon(if (dark) R.drawable.ic_l_sun else R.drawable.ic_l_moon, 20.dp, MaterialTheme.colorScheme.onSurface)
            }
        }
        HorizontalDivider(color = k.border)
    }
}

@Composable
private fun Logo(size: Int = 14) {
    // اللوجو في الموقع direction: ltr
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            KIcon(R.drawable.ic_ball, (size + 11).dp, MaterialTheme.colorScheme.primary)
            Text(
                buildAnnotatedString {
                    append("KORA ")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("CHALLENGE") }
                },
                fontSize = size.sp, fontWeight = FontWeight.Black
            )
        }
    }
}

/* ---------------- الهيرو ---------------- */
@Composable
private fun Hero(onStart: () -> Unit, onStats: () -> Unit) {
    val k = LocalKoraColors.current
    val green = MaterialTheme.colorScheme.primary
    Column(Modifier.fillMaxWidth().padding(start = 22.dp, end = 22.dp, top = 38.dp, bottom = 25.dp)) {
        // eyebrow
        Row(
            Modifier.clip(RoundedCornerShape(30.dp)).background(k.eyebrowBg)
                .border(1.dp, k.eyebrowBorder, RoundedCornerShape(30.dp)).padding(horizontal = 13.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Box(Modifier.size(7.dp).background(green, CircleShape))
            Text("عالم التحديات الكروية", color = green, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(20.dp))
        Text(
            buildAnnotatedString {
                append("إنت فاهم كورة\nولا ")
                withStyle(SpanStyle(color = green)) { append("مجرد مشجع؟") }
            },
            fontSize = 38.sp, lineHeight = 51.sp, fontWeight = FontWeight.Black
        )
        Spacer(Modifier.height(15.dp))
        Text(
            "اختبر معلوماتك الكروية، اكتشف مستواك، وتحدى نفسك في مجموعة من أقوى ألعاب كرة القدم.",
            color = k.muted, fontSize = 13.sp, lineHeight = 22.sp
        )
        Spacer(Modifier.height(26.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PrimaryBtn("ابدأ التحدي", onStart)
            Row(
                Modifier.clip(RoundedCornerShape(10.dp)).background(if (k.dark) Color(0xFF101713) else MaterialTheme.colorScheme.surface)
                    .border(1.dp, if (k.dark) Color(0xFF34483A) else k.border, RoundedCornerShape(10.dp))
                    .clickable(onClick = onStats).padding(horizontal = 23.dp, vertical = 11.dp)
            ) { Text("إحصائياتي", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold) }
        }
        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KIcon(R.drawable.ic_trophy, 18.dp, green)
            Text("العب، اجمع النقاط، واثبت إنك أسطورة", color = k.muted, fontSize = 12.sp)
        }
        HeroVisual()
    }
}

@Composable
private fun PrimaryBtn(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick).padding(horizontal = 23.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text, color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        KIcon(R.drawable.ic_arrow, 16.dp, MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
private fun HeroVisual() {
    val green = MaterialTheme.colorScheme.primary
    val t = rememberInfiniteTransition(label = "float")
    val dy by t.animateFloat(0f, -13f, infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "dy")
    val rot by t.animateFloat(-7f, 7f, infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "rot")
    val orbit1 = if (LocalKoraColors.current.dark) Color(0xFF2B593B) else Color(0xFFBFDCC9)
    val orbit2 = if (LocalKoraColors.current.dark) Color(0xFF1E3828) else Color(0xFFD5E8DC)

    Box(Modifier.fillMaxWidth().height(220.dp).padding(top = 12.dp), contentAlignment = Alignment.Center) {
        Box(
            Modifier.size(160.dp).background(
                Brush.radialGradient(listOf(Color(0x4013DB72), Color.Transparent)), CircleShape
            )
        )
        Canvas(Modifier.fillMaxSize()) {
            rotate(-25f, center) {
                val w = 240.dp.toPx(); val h = 90.dp.toPx()
                drawOval(orbit1, Offset(center.x - w / 2, center.y - h / 2), Size(w, h), style = Stroke(1.dp.toPx()))
            }
            rotate(40f, center) {
                val w = 260.dp.toPx(); val h = 130.dp.toPx()
                drawOval(orbit2, Offset(center.x - w / 2, center.y - h / 2), Size(w, h), style = Stroke(1.dp.toPx()))
            }
        }
        KIcon(
            R.drawable.ic_ball, 125.dp, green,
            Modifier.graphicsLayer { translationY = dy.dp.toPx(); rotationZ = rot }
        )
        // right:0 في الموقع = اليمين الفعلي = Start في RTL
        FloatingCard(R.drawable.ic_bolt, "التحدي بدأ!", "ورينا معلوماتك", Modifier.align(Alignment.TopStart).padding(top = 12.dp))
        FloatingCard(R.drawable.ic_trophy, "كن الأسطورة", "وصل لأعلى نتيجة", Modifier.align(Alignment.BottomEnd).padding(bottom = 8.dp))
    }
}

@Composable
private fun FloatingCard(icon: Int, title: String, sub: String, modifier: Modifier) {
    val k = LocalKoraColors.current
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier.clip(shape).background(MaterialTheme.colorScheme.surface.copy(alpha = .93f))
            .border(1.dp, if (k.dark) Color(0xFF304735) else k.border, shape).padding(horizontal = 9.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        KIcon(icon, 24.dp, MaterialTheme.colorScheme.primary)
        Column {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(sub, fontSize = 10.sp, color = k.muted)
        }
    }
}

/* ---------------- الإحصائيات ---------------- */
@Composable
private fun StatsStrip(onLeaderboard: () -> Unit) {
    val k = LocalKoraColors.current
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 48.dp).clip(shape)
            .background(MaterialTheme.colorScheme.surface).border(1.dp, k.border, shape)
            .padding(horizontal = 8.dp, vertical = 17.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        StatItem(R.drawable.ic_gamepad, KoraStore.stats.gamesPlayed.toString(), "جولات لعبتها", Modifier.weight(1f))
        StatItem(R.drawable.ic_trophy, "شوف تصنيفك العالمي", null, Modifier.weight(1f), small = true, onClick = onLeaderboard)
        StatItem(R.drawable.ic_bolt, KoraStore.stats.totalPoints.toString(), "مجموع النقاط", Modifier.weight(1f))
    }
}

@Composable
private fun StatItem(icon: Int, value: String, label: String?, modifier: Modifier, small: Boolean = false, onClick: (() -> Unit)? = null) {
    val k = LocalKoraColors.current
    Column(
        modifier.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(k.statIconBg), contentAlignment = Alignment.Center) {
            KIcon(icon, 18.dp, MaterialTheme.colorScheme.primary)
        }
        Text(value, fontSize = if (small) 12.sp else 18.sp, fontWeight = if (small) FontWeight.Bold else FontWeight.ExtraBold,
            textAlign = TextAlign.Center, lineHeight = if (small) 18.sp else 24.sp)
        if (label != null) Text(label, fontSize = 10.sp, color = k.muted)
    }
}

/* ---------------- عنوان القسم وتحدي اليوم ---------------- */
@Composable
private fun SectionHeading() {
    val k = LocalKoraColors.current
    val green = MaterialTheme.colorScheme.primary
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text("اختار لعبتك", color = green, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                buildAnnotatedString {
                    append("التحديات ")
                    withStyle(SpanStyle(color = green)) { append("الكروية") }
                },
                fontSize = 26.sp, lineHeight = 39.sp, fontWeight = FontWeight.Black
            )
            Text("كل لعبة فيها تحدي مختلف. تفتكر هتقدر تحلهم كلهم؟", color = k.muted, fontSize = 11.sp)
        }
        KIcon(R.drawable.ic_ball, 34.dp, green.copy(alpha = .8f))
    }
}

@Composable
private fun DailyBanner(game: Game, done: Boolean, onClick: () -> Unit) {
    val k = LocalKoraColors.current
    val shape = RoundedCornerShape(16.dp)
    val surface = MaterialTheme.colorScheme.surface
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 18.dp).clip(shape)
            .background(Brush.horizontalGradient(0f to Gold.copy(alpha = .16f), .6f to surface, 1f to surface))
            .border(1.dp, Gold.copy(alpha = .35f), shape).clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(k.surface2), contentAlignment = Alignment.Center) {
            KIcon(R.drawable.ic_calendar, 26.dp, Gold)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(game.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(game.description, fontSize = 12.sp, color = k.muted, lineHeight = 18.sp)
        }
        Box(
            Modifier.clip(CircleShape)
                .background(if (done) Color.Transparent else Gold)
                .then(if (done) Modifier.border(1.dp, k.border, CircleShape) else Modifier)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(if (done) "تم ✓" else game.cta, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                color = if (done) k.muted else Color(0xFF04110A))
        }
    }
}

/* ---------------- كارت اللعبة ---------------- */
private fun Modifier.artBackground(art: Art) = drawBehind {
    val cx = size.width * 0.5f
    val cy = size.height * 0.45f
    val r = hypot(maxOf(cx, size.width - cx), maxOf(cy, size.height - cy))
    drawRect(
        Brush.radialGradient(
            colorStops = arrayOf(0f to art.c1, 0.65f to art.c2, 1f to art.c3),
            center = Offset(cx, cy), radius = r
        )
    )
}

@Composable
private fun GameCard(game: Game, modifier: Modifier, onClick: () -> Unit) {
    val k = LocalKoraColors.current
    val green = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(15.dp)
    Column(
        modifier.clip(shape).background(MaterialTheme.colorScheme.surface).border(1.dp, k.border, shape).clickable(onClick = onClick)
    ) {
        Box(Modifier.fillMaxWidth().height(84.dp).clip(RectangleShape).artBackground(game.art), contentAlignment = Alignment.Center) {
            if (game.number.isNotEmpty()) {
                Text(
                    game.number, fontSize = 70.sp, fontWeight = FontWeight.Black, color = Color(0x0AFFFFFF),
                    modifier = Modifier.align(Alignment.BottomEnd).offset(y = 20.dp).padding(horizontal = 12.dp)
                )
            }
            if (game.label != null) {
                Text(
                    game.label, color = Color.White, fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp).clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1F9C57)).padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
            KIcon(game.icon, 54.dp, if (game.tintIcon) Color(0xFF24E982) else null)
        }
        Column(Modifier.weight(1f).padding(start = 11.dp, end = 11.dp, top = 10.dp, bottom = 12.dp)) {
            Text(game.category, color = green, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
            Text(game.title, fontSize = 15.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(game.description, color = k.muted, fontSize = 11.sp, lineHeight = 20.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                game.meta.forEach { m ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        KIcon(m.icon, 11.dp, green)
                        Text(m.text, color = k.metaText, fontSize = 9.sp)
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(10.dp))
            val bshape = RoundedCornerShape(8.dp)
            Row(
                Modifier.fillMaxWidth().clip(bshape).background(k.btnBg).border(1.dp, k.btnBorder, bshape).padding(10.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(game.cta, color = k.btnText, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                KIcon(R.drawable.ic_arrow, 16.dp, k.btnText)
            }
        }
    }
}

/* ---------------- البانر والفوتر ---------------- */
@Composable
private fun ChallengeBanner(onStart: () -> Unit) {
    val k = LocalKoraColors.current
    val green = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(17.dp)
    val end = if (k.dark) Color(0xFF0E1711) else Color(0xFFF8FBF9)
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 45.dp).clip(shape)
            .background(Brush.horizontalGradient(0f to k.bannerStart, .75f to end, 1f to end))
            .border(1.dp, k.bannerBorder, shape).padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KIcon(R.drawable.ic_trophy, 36.dp, green)
            Column {
                Text("التحدي الحقيقي", color = green, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    buildAnnotatedString {
                        append("هل تقدر تحقق ")
                        withStyle(SpanStyle(color = green)) { append("100%؟") }
                    },
                    fontSize = 20.sp, lineHeight = 32.sp, fontWeight = FontWeight.Black
                )
                Text("ابدأ بالسهل، طور مستواك، وافتح أصعب التحديات.", color = k.muted, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(14.dp))
        PrimaryBtn("ابدأ الآن", onStart, Modifier.fillMaxWidth())
    }
}

@Composable
private fun Footer() {
    val k = LocalKoraColors.current
    Column(Modifier.fillMaxWidth()) {
        HorizontalDivider(color = k.border)
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Logo(14)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("صُنع لعشاق كرة القدم", color = k.muted, fontSize = 11.sp)
                KIcon(R.drawable.ic_heart, 13.dp, MaterialTheme.colorScheme.primary)
            }
        }
    }
}
