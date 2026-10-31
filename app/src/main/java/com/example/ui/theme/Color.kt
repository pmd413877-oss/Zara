package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Backgrounds
val ZaraBackground = Color(0xFF080612)
val ZaraSurface = Color(0xFF120E24)
val ZaraSurfaceVariant = Color(0xFF1A1434)
val ZaraCardBg = Color(0xFF16122C)
val ZaraCardBorder = Color(0xFF2A2250)

// Accents
val ZaraPurple = Color(0xFFA855F7)
val ZaraElectricPurple = Color(0xFF8B5CF6)
val ZaraCyan = Color(0xFF00E5FF)
val ZaraBlue = Color(0xFF38BDF8)
val ZaraPink = Color(0xFFEC4899)
val ZaraGreen = Color(0xFF10B981)
val ZaraRed = Color(0xFFEF4444)

// Text Colors
val ZaraTextPrimary = Color(0xFFFFFFFF)
val ZaraTextSecondary = Color(0xFF94A3B8)
val ZaraTextMuted = Color(0xFF64748B)

// Glassmorphism overlays
val GlassOverlay = Color(0x331E1838)
val GlassBorder = Color(0x338B5CF6)

// Gradients
val ZaraPurpleBlueGradient = Brush.linearGradient(
    colors = listOf(ZaraPurple, ZaraCyan)
)

val ZaraOrbGradient = Brush.radialGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        ZaraCyan,
        ZaraElectricPurple,
        ZaraSurface
    )
)

val ZaraCardGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1A1436),
        Color(0xFF110D24)
    )
)
