package com.example.domain.trading

data class TradingLesson(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val category: String,
    val emoji: String,
    val summaryBn: String,
    val summaryEn: String,
    val fullContentBn: String,
    val fullContentEn: String,
    val keyTakeawayBn: String,
    val keyTakeawayEn: String
)

data class TradingQuiz(
    val id: Int,
    val questionBn: String,
    val questionEn: String,
    val optionsBn: List<String>,
    val optionsEn: List<String>,
    val correctIndex: Int,
    val explanationBn: String,
    val explanationEn: String
)

data class RiskCalculationResult(
    val riskAmount: Double,
    val positionSizeUnits: Double,
    val totalInvestment: Double,
    val targetPriceFor1To2: Double,
    val targetProfit: Double
)

object TradingMentorRepository {

    fun calculateRisk(
        capital: Double,
        riskPercent: Double,
        entryPrice: Double,
        stopLossPrice: Double
    ): RiskCalculationResult {
        val riskAmount = capital * (riskPercent / 100.0)
        val priceDiff = kotlin.math.abs(entryPrice - stopLossPrice)
        val units = if (priceDiff > 0.0) riskAmount / priceDiff else 0.0
        val totalInvestment = units * entryPrice
        val isLong = entryPrice >= stopLossPrice
        val targetPrice = if (isLong) entryPrice + (priceDiff * 2.0) else entryPrice - (priceDiff * 2.0)
        val targetProfit = riskAmount * 2.0

        return RiskCalculationResult(
            riskAmount = riskAmount,
            positionSizeUnits = units,
            totalInvestment = totalInvestment,
            targetPriceFor1To2 = targetPrice,
            targetProfit = targetProfit
        )
    }

    val lessons: List<TradingLesson> = listOf(
        TradingLesson(
            id = "lesson_1",
            titleBn = "১. ট্রেডিংয়ের মূল ভিত্তি ও সাইকোলজি",
            titleEn = "1. Trading Fundamentals & Mindset",
            category = "Basics",
            emoji = "📈",
            summaryBn = "স্পট বনাম ফিউচার্স, লেভারেজের ঝুঁকি এবং ডিসিপ্লিন রক্ষার মূল নিয়ম।",
            summaryEn = "Spot vs Futures, leverage risks, and trading discipline rules.",
            fullContentBn = """
                ট্রেডিং একটি সুনির্দিষ্ট সম্ভাব্যতা বা প্রবাবিলিটি ভিত্তিক ব্যবসা।
                
                ১. স্পট বনাম ফিউচার্স:
                - স্পট ট্রেডিং (Spot): আপনি সরাসরি অ্যাসেট ক্রয় করছেন। দাম কমলে লস কিন্তু লিকুইডেশন হওয়ার ভয় নেই।
                - ফিউচার্স ট্রেডিং (Futures): ব্রোকার থেকে ঋণ (Leverage) নিয়ে ট্রেড। ভুল ট্রেডে দ্রুত ব্যালেন্স শূন্য হতে পারে। নতুনদের কখনো 2x-3x এর বেশি ব্যবহার করা উচিত নয়।
                
                ২. ট্রেডিং সাইকোলজি:
                - FOMO (Fear of Missing Out): হঠাৎ দাম বাড়তে দেখে না বুঝে লাফিয়ে পড়া বন্ধ করুন।
                - রিভেঞ্জ ট্রেডিং (Revenge Trading): লস হওয়ার পর রেগে গিয়ে সাথে সাথে বড় লট নিয়ে ট্রেড দেওয়া সবচেয়ে বড় ভুল।
                - ডিসিপ্লিন: নিয়ম ছাড়া একটি ট্রেডও নেওয়া যাবে না।
            """.trimIndent(),
            fullContentEn = """
                Trading is a business of probabilities, not gambling.
                
                1. Spot vs Futures:
                - Spot: You own the underlying asset. No liquidation risk. Ideal for beginners.
                - Futures: Trading contracts with leverage. High reward, but extreme risk of liquidation. Never exceed 2x-3x when learning.
                
                2. Psychology:
                - Avoid FOMO (buying after an asset pumped).
                - Stop Revenge Trading (trying to win back losses immediately with emotion).
                - Patience and strict discipline separate the 5% winners from the rest.
            """.trimIndent(),
            keyTakeawayBn = "ট্রেডিংয়ে লাভ করার আগে ক্যাপিটাল বাঁচানো শিখতে হবে। ক্যাপিটাল শেষ হলে সুযোগও শেষ।",
            keyTakeawayEn = "Capital preservation is rule #1. Without capital, you have no game."
        ),
        TradingLesson(
            id = "lesson_2",
            titleBn = "২. ক্যান্ডেলস্টিক প্যাটার্ন মাস্টারক্লাস",
            titleEn = "2. Candlestick Patterns Masterclass",
            category = "Technical",
            emoji = "🕯️",
            summaryBn = "হ্যামার, বুলিশ এনগালফিং, দোজি এবং শুটিং স্টার চেনার উপায়।",
            summaryEn = "Identifying Hammer, Bullish Engulfing, Doji, and Shooting Star.",
            fullContentBn = """
                ক্যান্ডেলস্টিক হলো মার্কেটের ক্রেতা (Bulls) ও বিক্রেতা (Bears) এর যুদ্ধক্ষেত্র।
                
                ১. হ্যামার (Hammer):
                - দেখতে হাতুড়ির মতো। নিচের দিকে লম্বা উইক (Wick) এবং উপরে ছোট বডি।
                - এটি ডাউনট্রেন্ডের শেষে সাপোর্ট লেভেলে তৈরি হলে শক্তিশালী রিভার্সাল সিগন্যাল দেয়।
                
                ২. বুলিশ এনগালফিং (Bullish Engulfing):
                - একটি ছোট লাল ক্যান্ডেলকে পরের বড় সবুজ ক্যান্ডেল সম্পূর্ণ গিলে নেয়।
                - ক্রেতারা পুরোপুরি মার্কেটের নিয়ন্ত্রণ নিয়েছে এমন ইঙ্গিত বহন করে।
                
                ৩. দোজি (Doji):
                - ওপেন এবং ক্লোজ প্রাইস প্রায় সমান।
                - ক্রেতা এবং বিক্রেতা উভয়েই দ্বিধাদ্বন্দ্বে আছে (Indecision)। ট্রপ হওয়া এড়াতে পরের ক্যান্ডেল কনফার্মেশন জরুরি।
                
                ৪. শুটিং স্টার (Shooting Star):
                - আপট্রেন্ডের শীর্ষে লম্বা ওপরের উইক। এটি বিয়ারিশ রিভার্সালের পূর্বাভাস দেয়।
            """.trimIndent(),
            fullContentEn = """
                Candlesticks reveal buyer (Bull) vs seller (Bear) dominance.
                
                1. Hammer:
                - Small body at the top with a long lower shadow (wick at least 2x the body).
                - Found at bottom of downtrends; indicates strong buying rejection.
                
                2. Bullish Engulfing:
                - A green candle completely covers the previous red candle's body.
                - Signals buyers have taken control of the market.
                
                3. Doji:
                - Open and close are almost identical.
                - Shows indecision in the market. Wait for confirmation.
                
                4. Shooting Star:
                - Long upper wick at the peak of an uptrend. Signals bearish reversal.
            """.trimIndent(),
            keyTakeawayBn = "ক্যান্ডেলস্টিক একা যথেষ্ট নয়; সাপোর্ট বা রেজিস্ট্যান্সের সাথে মিলিয়ে ব্যবহার করলে উইন রেট দ্বিগুণ হয়।",
            keyTakeawayEn = "Never trade candlesticks in isolation; combine them with key Support/Resistance levels."
        ),
        TradingLesson(
            id = "lesson_3",
            titleBn = "৩. টেকনিক্যাল ইন্ডিকেটর (RSI, MACD, EMA)",
            titleEn = "3. Indicators: RSI, MACD, and EMAs",
            category = "Indicators",
            emoji = "📊",
            summaryBn = "ওভারবট ও ওভারসোল্ড জোন, সিগন্যাল লাইন ক্রসওভার এবং গোল্ডেন ক্রস।",
            summaryEn = "Overbought/oversold levels, signal line crossovers, and golden crosses.",
            fullContentBn = """
                ইন্ডিকেটর প্রাইসের গাণিতিক হিসাব প্রকাশ করে।
                
                ১. RSI (Relative Strength Index):
                - স্কেল ০ থেকে ১০০।
                - RSI < ৩০: মার্কেট Oversold (অতিরিক্ত বিক্রি হয়েছে, দাম বৃদ্ধির সম্ভাবনা)।
                - RSI > ৭০: মার্কেট Overbought (অতিরিক্ত কেনা হয়েছে, সংশোধন বা পতনের সম্ভাবনা)।
                - বুলিশ ডাইভারজেন্স (Bullish Divergence): প্রাইস লোয়ার লো বানাচ্ছে কিন্তু RSI হায়ার লো বানাচ্ছে—এটি শক্তিশালী রিভার্সাল সংকেত।
                
                ২. MACD (Moving Average Convergence Divergence):
                - MACD লাইন যখন সিগন্যাল লাইনের উপরে উঠে, তখন তা বাই সিগন্যাল।
                - নিচে নামলে সেল সিগন্যাল।
                
                ৩. Exponential Moving Average (EMA 20, 50, 200):
                - প্রাইস 200 EMA এর উপরে থাকা মানে মার্কেট লং টার্ম আপট্রেন্ডে আছে।
                - গোল্ডেন ক্রস (Golden Cross): যখন 50 EMA নিচ থেকে 200 EMA কে ক্রস করে উপরে যায়।
            """.trimIndent(),
            fullContentEn = """
                Indicators visualize mathematical calculations of price and momentum.
                
                1. RSI (Relative Strength Index):
                - Below 30: Oversold territory (potential bounce/buying opportunity).
                - Above 70: Overbought territory (pullback likely).
                - RSI Divergence provides high-probability trend reversal cues.
                
                2. MACD:
                - Fast line crossing above Signal line indicates bullish momentum.
                - Crossing below indicates bearish momentum.
                
                3. EMA 20/50/200:
                - EMA 200 indicates macro trend direction. Above = Bullish, Below = Bearish.
                - Golden Cross: 50 EMA crosses above 200 EMA.
            """.trimIndent(),
            keyTakeawayBn = "ইন্ডিকেটর প্রাইসের পেছনে চলে (Lagging)। সর্বদা আগে প্রাইস অ্যাকশন দেখুন, পরে ইন্ডিকেটর দিয়ে কনফার্ম করুন।",
            keyTakeawayEn = "Indicators lag price action. Always prioritize pure price action and use indicators as confirmation."
        ),
        TradingLesson(
            id = "lesson_4",
            titleBn = "৪. রিস্ক ম্যানেজমেন্ট (১-২% রুল ও স্টপ-লস)",
            titleEn = "4. Risk Management (1-2% Rule & Stop-Loss)",
            category = "Risk",
            emoji = "🛡️",
            summaryBn = "ট্রেডিংয়ে বেঁচে থাকার একমাত্র চাবিকাঠি: স্টপ-লস এবং রিস্ক-টু-রিওয়ার্ড।",
            summaryEn = "The holy grail of longevity: Stop-Loss, position sizing, and R:R ratio.",
            fullContentBn = """
                ট্রেডিংয়ে ৯০% মানুষ ব্যর্থ হয় শুধুমাত্র রিস্ক ম্যানেজমেন্ট না জানার কারণে।
                
                ১. ১-২% রুল (1-2% Rule):
                - একটি একক ট্রেডে আপনার মোট মূলধনের ১% থেকে সর্বোচ্চ ২% এর বেশি রিস্ক নেবেন না।
                - উদাহরণ: আপনার মূলধন $১,০০০ হলে প্রতি ট্রেডে সর্বোচ্চ লস সীমা হবে $১০ থেকে $২০।
                
                ২. স্টপ-লস (Stop-Loss):
                - এন্ট্রি নেওয়ার আগেই স্টপ-লস সেট করুন।
                - কখনো ম্যানুয়ালি আশা করে বসে থাকবেন না যে মার্কেট ঘুরে দাঁড়াবে।
                
                ৩. রিস্ক-টু-রিওয়ার্ড (Risk:Reward Ratio):
                - ন্যূনতম 1:2 অনুপাতের ট্রেড নিন। অর্থাৎ $১০ লস রিস্কের বিপরীতে লাভ আশা করতে হবে $২০।
                - আপনি যদি মাত্র ৪০% ট্রেডেও জিতেন, তাও মাস শেষে লাভে থাকবেন!
            """.trimIndent(),
            fullContentEn = """
                Risk management is what separates gambling from professional trading.
                
                1. The 1-2% Capital Rule:
                - Never risk more than 1% to 2% of total account capital on a single trade.
                - On a $1,000 account, maximum risk per trade is $10 - $20.
                
                2. Stop-Loss Discipline:
                - Define invalidation BEFORE entering.
                - Never move your stop-loss backwards hoping for a recovery.
                
                3. Risk-to-Reward (R:R):
                - Target at least 1:2 R:R. Risking $10 to make $20+.
                - With 1:2 R:R, even a 40% win rate makes you consistently profitable!
            """.trimIndent(),
            keyTakeawayBn = "স্টপ-লস হলো আপনার মূলধনের জীবনবীমা। স্টপ-লস ছাড়া ট্রেড করা মানে ব্রেক ছাড়া গাড়ি চালানো।",
            keyTakeawayEn = "A stop loss is insurance for your capital. Trading without one is like driving without brakes."
        ),
        TradingLesson(
            id = "lesson_5",
            titleBn = "৫. সাপোর্ট, রেজিস্ট্যান্স এবং ব্রেকআউট স্ট্র্যাটেজি",
            titleEn = "5. Support, Resistance & Breakouts",
            category = "Strategy",
            emoji = "🎯",
            summaryBn = "কী-লেভেল আঁকা, ফেকআউট (Fakeout) চেনা এবং রিটেস্টে এন্ট্রি নেওয়া।",
            summaryEn = "Drawing key levels, identifying fakeouts, and retest entries.",
            fullContentBn = """
                ১. সাপোর্ট (Support):
                - ফ্লোর বা মেঝের মতো। যেখানে প্রাইস এসে নিচে নামা বন্ধ করে বারবার বাউন্স করে।
                - সাপোর্ট ভেঙে গেলে তা ভবিষ্যতের রেজিস্ট্যান্সে পরিণত হয় (Role Reversal)।
                
                ২. রেজিস্ট্যান্স (Resistance):
                - ছাদ বা সিলিং এর মতো। যেখানে এসে সেলারদের চাপে দাম নামতে শুরু করে।
                
                ৩. ব্রেকআউট ও রিটেস্ট (Breakout & Retest):
                - ব্রেকআউট হওয়ার সাথে সাথে ফোমোতে বাই করবেন না।
                - ব্রেক হওয়া লেভেলে প্রাইস পুনরায় রিটেস্ট (Retest) করে বাউন্স করলে কনফার্মেশনে এন্ট্রি নিন। এটি ফেকআউট থেকে বাঁচায়।
            """.trimIndent(),
            fullContentEn = """
                1. Support:
                - A price level where buyers consistently step in to prevent further decline.
                - Once broken, support turns into future resistance.
                
                2. Resistance:
                - A price ceiling where selling pressure exceeds buying pressure.
                
                3. Breakout & Retest Strategy:
                - Don't buy immediately upon a breakout (beware of fakeouts / bull traps).
                - Wait for price to retest the broken level as new support before entering.
            """.trimIndent(),
            keyTakeawayBn = "সাপোর্টে বাই করুন এবং রেজিস্ট্যান্সে সেল করুন। ট্রেন্ডের বিপরীতে কখনো লড়াই করবেন না।",
            keyTakeawayEn = "Buy at support, take profit at resistance. Never fight the prevailing trend."
        )
    )

    val quizzes: List<TradingQuiz> = listOf(
        TradingQuiz(
            id = 1,
            questionBn = "ট্রেডিংয়ে ১-২% রুল বলতে আসলে কী বোঝায়?",
            questionEn = "What does the 1-2% rule in trading mean?",
            optionsBn = listOf(
                "প্রতি ট্রেডে মাত্র ১-২ ডলার ইনভেস্ট করা",
                "একটি একক ট্রেডে মোট ক্যাপিটালের ১-২% এর বেশি লস না নেওয়া",
                "প্রতিদিন ১-২% লাভ হতেই হবে",
                "মাসে মাত্র ২টি ট্রেড করা"
            ),
            optionsEn = listOf(
                "Invest only $1-$2 per trade",
                "Never risk losing more than 1-2% of total account balance on one trade",
                "Target guaranteed 1-2% daily profit",
                "Trade only twice a month"
            ),
            correctIndex = 1,
            explanationBn = "সঠিক! ১-২% রুলের মূল উদ্দেশ্য হলো একটানা কয়েকটি ট্রেডে লস হলেও যেন আপনার মূলধন সুরক্ষিত থাকে।",
            explanationEn = "Correct! The 1-2% rule ensures you can withstand drawdowns without blowing up your account."
        ),
        TradingQuiz(
            id = 2,
            questionBn = "RSI মান যখন ২০-এ নেমে আসে, তখন মার্কেট সাধারণত কোন অবস্থায় থাকে?",
            questionEn = "When RSI drops down to 20, what state is the market typically in?",
            optionsBn = listOf(
                "ওভারবট (Overbought - অতিরিক্ত কেনা হয়েছে)",
                "ওভারসোল্ড (Oversold - অতিরিক্ত বিক্রি হয়েছে, বাউন্স হতে পারে)",
                "মার্কেট বন্ধ হয়ে গেছে",
                "লিকুইডেশন শেষ"
            ),
            optionsEn = listOf(
                "Overbought (overly purchased)",
                "Oversold (oversold, potential rebound/bounce)",
                "Market is closed",
                "Zero volume"
            ),
            correctIndex = 1,
            explanationBn = "সঠিক! RSI < ৩০ হলে মার্কেট Oversold হিসেবে চিহ্নিত হয় এবং প্রাইস রিবাউন্ড করার সম্ভাবনা তৈরি হয়।",
            explanationEn = "Correct! RSI below 30 signals oversold conditions where a relief rally or bounce is common."
        ),
        TradingQuiz(
            id = 3,
            questionBn = "ডাউনট্রেন্ডের নিচে যদি লম্বা নিচের উইকযুক্ত হ্যামার (Hammer) ক্যান্ডেল দেখা যায়, এটি কী নির্দেশ করে?",
            questionEn = "A Hammer candle with a long lower shadow appearing at the bottom of a downtrend indicates:",
            optionsBn = listOf(
                "দাম আরও নিচে নামবে",
                "ক্রেতারা সেলারদের পুশব্যাক করেছে এবং দাম রিভার্সাল হতে পারে",
                "মার্কেটে কোনো ভলিউম নেই",
                "সেলাররা সম্পূর্ণ নিয়ন্ত্রণে আছে"
            ),
            optionsEn = listOf(
                "Price will crash lower",
                "Buyers rejected lower prices and an upward reversal is possible",
                "Market has zero volume",
                "Sellers are dominant"
            ),
            correctIndex = 1,
            explanationBn = "সঠিক! হ্যামারের লম্বা নিচের উইক প্রমাণ করে সেলাররা দাম কমানোর চেষ্টা করেছিল কিন্তু বায়াররা এসে দাম উপরে টেনে তুলেছে।",
            explanationEn = "Correct! The long lower wick proves buyers stepped in aggressively to reject lower prices."
        )
    )
}
