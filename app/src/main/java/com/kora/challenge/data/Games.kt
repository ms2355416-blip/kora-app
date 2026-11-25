package com.kora.challenge.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.kora.challenge.R

/** تدرّج خلفية كارت اللعبة: radial-gradient(circle at 50% 45%, c1, c2 65%, c3) */
enum class Art(val c1: Color, val c2: Color, val c3: Color = Color(0xFF0E1711)) {
    Player(Color(0xFF1B6538), Color(0xFF10291B)),
    Sprint(Color(0xFF7A2C4A), Color(0xFF2A1220)),
    Club(Color(0xFF315D24), Color(0xFF172B18)),
    Grid(Color(0xFF1F4F5C), Color(0xFF12262B)),
    Country(Color(0xFF174D69), Color(0xFF112731)),
    Team(Color(0xFF654E20), Color(0xFF2A2415)),
    Career(Color(0xFF1F5A3A), Color(0xFF12281D))
}

data class Meta(@DrawableRes val icon: Int, val text: String)

/** كل لعبة في الموقع. لإضافة/تعديل لعبة عدّل القايمة تحت بس. */
data class Game(
    val id: String,          // بيتستخدم في التنقل
    val webPage: String,     // الصفحة المقابلة في الموقع الأصلي
    @DrawableRes val icon: Int,
    val tintIcon: Boolean = true,   // false = الأيقونة بألوانها الأصلية
    val art: Art = Art.Player,
    val number: String = "",
    val label: String? = null,      // الشارة فوق الكارت (ONLINE / الأكثر لعبًا)
    val category: String,
    val title: String,
    val description: String,
    val meta: List<Meta> = emptyList(),
    val cta: String = "العب دلوقتي"
)

object Games {
    val daily = Game(
        id = "daily", webPage = "daily.html", icon = R.drawable.ic_calendar, category = "كل يوم",
        title = "تحدي اليوم",
        description = "3 أسئلة معلومات عامة كل يوم، وكل يوم بتزود سلسلتك.", cta = "ابدأ"
    )

    val all: List<Game> = listOf(
        Game("myteem", "myteem.html", R.drawable.ic_team, art = Art.Team, number = "02", label = "ONLINE",
            category = "تحدي التكتيك", title = "كوّن فريقك",
            description = "أنت المدرب دلوقتي! اختار حارس المرمى والدفاع والوسط والهجوم، وابنِ تشكيلتك المفضلة حسب الخطة اللي تناسبك. هل تقدر تجمع فريق متكامل ومتوازن؟",
            meta = listOf(Meta(R.drawable.ic_list, "خطط مختلفة"), Meta(R.drawable.ic_trophy, "تشكيلتك أنت"))),
        Game("careermode", "careermode.html", R.drawable.ic_art_career, tintIcon = false, art = Art.Career, number = "07",
            category = "مسيرة", title = "كارير مود",
            description = "اصنع لاعبك وابدأ من الدرجة الثالثة في إنجلترا أو التانية في إسبانيا. العب المواسم، سجّل وصنّع، وانتقل لأندية أكبر.",
            meta = listOf(Meta(R.drawable.ic_bolt, "مواسم كاملة"), Meta(R.drawable.ic_question, "عروض انتقال")),
            cta = "ابدأ مسيرتك"),
        Game("guess-player", "guess-player.html", R.drawable.ic_player, art = Art.Player, number = "01", label = "الأكثر لعبًا",
            category = "تحدي المعلومات", title = "خمن اللاعب",
            description = "اكتشف هوية نجم كرة القدم من خلال تلميحات عن ناديه وجنسيته ومسيرته. اختار الإجابة الصح واجمع النقاط، وكل ما تتقدم هتواجه أسئلة أصعب!",
            meta = listOf(Meta(R.drawable.ic_target, "4 مستويات"), Meta(R.drawable.ic_question, "5 أسئلة"))),
        Game("players90", "players90.html", R.drawable.ic_bolt, art = Art.Sprint, number = "03",
            category = "تحدي السرعة", title = "تحدي ال 90",
            description = "هتشوف صور لاعبين مشهورين، وعليك تكتب اسم كل واحد بأسرع ما تقدر. العداد شغال، وكل اسم صح بيزود نتيجتك. تقدر تجيب كام لاعب قبل ما الوقت يخلص؟",
            meta = listOf(Meta(R.drawable.ic_bolt, "90 ثانية"), Meta(R.drawable.ic_player, "200 لاعب"))),
        Game("club-quiz", "club-quiz.html", R.drawable.ic_heart, art = Art.Club, number = "04",
            category = "تحدي الأندية", title = "اختبر معلوماتك عن فريقك المفضل",
            description = "اختار ناديك المفضل من بين 20 ناديًا حول العالم، واستعد تختبر معلوماتك عن تاريخه ونجومه وبطولاته.",
            meta = listOf(Meta(R.drawable.ic_team, "20 ناديًا"), Meta(R.drawable.ic_question, "أسئلة خاصة بكل نادي")),
            cta = "اختار ناديك"),
        Game("grid", "grid.html", R.drawable.ic_grid, art = Art.Grid, number = "06",
            category = "تحدي الشبكة", title = "شبكة الأندية",
            description = "شبكة 3×3 فيها أندية ومنتخبات على المحاور. اكتب لاعب لعب للفريقين في كل مربع، وافتح أطوار ومكافآت بنقاطك.",
            meta = listOf(Meta(R.drawable.ic_grid, "9 مربعات"), Meta(R.drawable.ic_star, "أطوار مقفولة"))),
        Game("country", "country.html", R.drawable.ic_globe, art = Art.Country, number = "05",
            category = "تحدي المنتخبات", title = "خمن المنتخب",
            description = "اختبر معلوماتك عن منتخبات العالم! تعرّف على المنتخب من خلال أعلامه وألوانه ونجومه وإنجازاته، وشوف تقدر تميّز كام منتخب من غير مساعدة.",
            meta = listOf(Meta(R.drawable.ic_globe, "منتخبات العالم"), Meta(R.drawable.ic_brain, "اختبر ذاكرتك")))
            ,
        Game("draw", "draw.html", R.drawable.ic_art_draw, tintIcon = false, art = Art.Career, number = "08",
            category = "تحدي أونلاين", title = "تحدي رسم اللاعبين",
            description = "ادخل غرفة بكود مع صاحبك: واحد يرسم لاعب والتاني يخمّن اسمه، والرسّام هو اللي يحكم صح ولا غلط. 4 جولات بتبدّلوا فيها الأدوار، والمخمّن الصح ياخد 60 نقطة.",
            meta = listOf(Meta(R.drawable.ic_list, "4 جولات"), Meta(R.drawable.ic_trophy, "+60 نقطة"))),
        Game("trivia", "trivia.html", R.drawable.ic_brain, art = Art.Team, number = "09", label = "ONLINE",
            category = "تحدي المعلومات", title = "تحدي المعلومات الكروية",
            description = "أسئلة اختيارات من كل حتة في كرة القدم! العب ضد الكمبيوتر أو ادخل غرفة بكود مع صاحبك، وجاوب أسرع منه واملا شريط النقاط قبله. 10 أسئلة و50 ثانية لكل سؤال.",
            meta = listOf(Meta(R.drawable.ic_question, "280+ سؤال"), Meta(R.drawable.ic_bolt, "50 ثانية")))
    )

    // شاشات في الموقع مش كروت في الرئيسية (هتتعمل في مراحل لاحقة)
    val extras = listOf(
        Game("leaderboard", "leaderboard.html", R.drawable.ic_trophy, category = "الترتيب",
            title = "الترتيب العالمي", description = "شوف تصنيفك بين اللاعبين."),
        Game("coach", "coach.html", R.drawable.ic_list, category = "مدرب",
            title = "مهنة مدرب", description = "ابدأ مسيرتك كمدرب.")
    )

    fun byId(id: String): Game? = (listOf(daily) + all + extras).firstOrNull { it.id == id }
}
