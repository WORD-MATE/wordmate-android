import 'package:flutter/material.dart';

/// Figma "Wordmate / 01 · Color" 팔레트.
class Palette {
  Palette._();

  static const gray01 = Color(0xFFFFFFFF);
  static const gray02 = Color(0xFFF8F9FA);
  static const gray03 = Color(0xFFEFF1F3);
  static const gray04 = Color(0xFFE8E9EA);
  static const gray06 = Color(0xFFE5E7EA);
  static const gray08 = Color(0xFFDFE3E6);
  static const gray10 = Color(0xFFD1D3D6);
  static const gray11 = Color(0xFFC4C9CF);
  static const gray12 = Color(0xFF737A83);
  static const gray14 = Color(0xFF5B626C);
  static const gray17 = Color(0xFF121417);

  static const lime01 = Color(0xFFB6E02A);
  static const lime02 = Color(0xFF96C814);

  static const yellow02 = Color(0xFFFFBF00);
  static const orange06 = Color(0xFFFF8000);
  static const orange07 = Color(0xFFD48500);

  static const red02 = Color(0xFFEF4452);
  static const red03 = Color(0xFFF03E3E);
  static const red04 = Color(0xFFC13D2C);

  static const blue02 = Color(0xFF3181F5);
  static const mint = Color(0xFF6BE3B5);
}

/// 화면에서 쓰는 의미 기반 컬러 토큰.
class AppColors {
  AppColors._();

  static const primary = Palette.lime01;
  static const primaryDark = Palette.lime02;
  static const onPrimary = Palette.gray17;

  static const background = Palette.gray01;
  static const surface = Palette.gray02;
  static const surfaceVariant = Palette.gray03;
  static const surfacePressed = Palette.gray06;
  static const divider = Palette.gray06;
  static const border = Palette.gray08;

  static const textPrimary = Palette.gray17;
  static const textSecondary = Palette.gray14;
  static const textTertiary = Palette.gray12;
  static const textDisabled = Palette.gray11;

  static const black = Palette.gray17;
  static const error = Palette.red03;
  static const errorStrong = Palette.red04;
  static const info = Palette.blue02;
  static const notification = Palette.red02;
  static const scrim = Color(0x80121417);

  static const gold = Palette.yellow02;
  static const silver = Palette.gray11;
  static const bronze = Color(0xFFD08A4C);

  static const heroGradient = LinearGradient(
    colors: [Palette.lime01, Palette.mint],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
  );
}

/// Figma "Wordmate / 02 · Typography" 타입 스케일.
/// 폰트 파일을 추가하면 [fontFamily] 만 바꾸면 된다.
class AppText {
  AppText._();

  static const String? fontFamily = null;

  static TextStyle _s(double size, double height, FontWeight weight, [double spacing = -0.01]) => TextStyle(
        fontFamily: fontFamily,
        fontSize: size,
        height: height / size,
        fontWeight: weight,
        letterSpacing: size * spacing,
        color: AppColors.textPrimary,
      );

  static final display = _s(56, 60, FontWeight.w800, -0.04);
  static final score = _s(48, 52, FontWeight.w800, -0.03);
  static final wordLarge = _s(32, 38, FontWeight.w800, -0.02);
  static final headline1 = _s(24, 32, FontWeight.w700, -0.02);
  static final headline2 = _s(22, 30, FontWeight.w700, -0.02);
  static final title1 = _s(20, 28, FontWeight.w700);
  static final title2 = _s(18, 26, FontWeight.w700);
  static final title3 = _s(17, 24, FontWeight.w600);
  static final body1Bold = _s(16, 24, FontWeight.w600);
  static final body1 = _s(16, 24, FontWeight.w400);
  static final body2Bold = _s(15, 22, FontWeight.w600);
  static final body2 = _s(15, 22, FontWeight.w400);
  static final label1Bold = _s(14, 20, FontWeight.w600);
  static final label1 = _s(14, 20, FontWeight.w400);
  static final label2Bold = _s(13, 18, FontWeight.w600);
  static final label2 = _s(13, 18, FontWeight.w400);
  static final caption1Bold = _s(12, 16, FontWeight.w600);
  static final caption1 = _s(12, 16, FontWeight.w400);
  static final caption2 = _s(11, 14, FontWeight.w500);
}

/// Figma "Wordmate / 03 · Spacing & Radius".
class AppRadius {
  AppRadius._();

  static const xs = BorderRadius.all(Radius.circular(4));
  static const sm = BorderRadius.all(Radius.circular(8));
  static const md = BorderRadius.all(Radius.circular(12));
  static const lg = BorderRadius.all(Radius.circular(16));
  static const xl = BorderRadius.all(Radius.circular(20));
  static const sheet = BorderRadius.vertical(top: Radius.circular(20));
}

class Gap {
  Gap._();

  static const screen = 20.0;
  static const h4 = SizedBox(height: 4);
  static const h8 = SizedBox(height: 8);
  static const h12 = SizedBox(height: 12);
  static const h16 = SizedBox(height: 16);
  static const h20 = SizedBox(height: 20);
  static const h24 = SizedBox(height: 24);
  static const w4 = SizedBox(width: 4);
  static const w8 = SizedBox(width: 8);
  static const w12 = SizedBox(width: 12);
}

ThemeData buildAppTheme() {
  return ThemeData(
    useMaterial3: true,
    fontFamily: AppText.fontFamily,
    colorScheme: ColorScheme.fromSeed(
      seedColor: AppColors.primary,
      primary: AppColors.primary,
      onPrimary: AppColors.onPrimary,
      surface: AppColors.background,
      onSurface: AppColors.textPrimary,
      error: AppColors.error,
    ),
    scaffoldBackgroundColor: AppColors.background,
    splashFactory: InkRipple.splashFactory,
    dividerColor: AppColors.divider,
    bottomSheetTheme: const BottomSheetThemeData(
      backgroundColor: AppColors.background,
      surfaceTintColor: Colors.transparent,
      shape: RoundedRectangleBorder(borderRadius: AppRadius.sheet),
      showDragHandle: false,
    ),
    dialogTheme: const DialogThemeData(
      backgroundColor: AppColors.background,
      surfaceTintColor: Colors.transparent,
      shape: RoundedRectangleBorder(borderRadius: AppRadius.xl),
    ),
    snackBarTheme: SnackBarThemeData(
      behavior: SnackBarBehavior.floating,
      backgroundColor: AppColors.black,
      contentTextStyle: AppText.label1.copyWith(color: Palette.gray01),
      shape: const RoundedRectangleBorder(borderRadius: AppRadius.md),
    ),
  );
}
