import 'package:flutter/material.dart';

import '../../data/store.dart';
import '../../utils/nav.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import 'terms_screen.dart';

class LoginScreen extends StatelessWidget {
  const LoginScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Gap.h16,
              Text('wordmate', style: AppText.title2.copyWith(fontWeight: FontWeight.w800)),
              const Spacer(flex: 5),
              const MateLogo(),
              const Spacer(flex: 6),
              Text.rich(
                TextSpan(children: [
                  const TextSpan(text: '매일 10분,\n단어로 '),
                  TextSpan(text: '겨루고', style: const TextStyle(decoration: TextDecoration.underline)),
                  const TextSpan(text: ' 성장하기'),
                ]),
                style: AppText.headline2,
              ),
              Gap.h24,
              AppButton(
                'DataGSM으로 로그인',
                kind: ButtonKind.secondary,
                onPressed: () {
                  store.login();
                  push(context, const TermsScreen());
                },
              ),
              Gap.h12,
            ],
          ),
        ),
      ),
    );
  }
}

/// 라임색 하이라이트 위에 얹힌 "mate" 워드마크.
class MateLogo extends StatelessWidget {
  const MateLogo({super.key});

  @override
  Widget build(BuildContext context) {
    return CustomPaint(
      painter: _HighlightPainter(),
      child: Text(
        'mate',
        style: AppText.display.copyWith(fontSize: 84, height: 88 / 84, fontWeight: FontWeight.w900),
      ),
    );
  }
}

class _HighlightPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    const inset = 6.0;
    canvas.drawRect(
      Rect.fromLTWH(-inset, size.height * 0.58, size.width + inset * 2, size.height * 0.2),
      Paint()..color = AppColors.primary,
    );
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
