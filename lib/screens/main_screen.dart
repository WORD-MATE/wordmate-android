import 'package:flutter/material.dart';

import '../utils/nav.dart';
import '../utils/theme.dart';
import '../widgets/layout.dart';
import 'home/home_screen.dart';
import 'wordpack/word_packs_screen.dart';

/// 하단 탭 5개(홈/단어팩/대결/랭킹/마이)를 가진 메인 화면.
class MainScreen extends StatelessWidget {
  const MainScreen({super.key});

  static const _tabs = [
    (Icons.home_rounded, '홈'),
    (Icons.view_agenda_rounded, '단어팩'),
    (Icons.sports_esports_rounded, '대결'),
    (Icons.emoji_events_rounded, '랭킹'),
    (Icons.person_rounded, '마이'),
  ];

  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder<int>(
      valueListenable: mainTab,
      builder: (context, index, _) => Scaffold(
        body: IndexedStack(
          index: index,
          children: [
            const HomeScreen(),
            const WordPacksScreen(),
            for (final (_, label) in _tabs.skip(2)) Center(child: Text(label, style: AppText.title1)),
          ],
        ),
        bottomNavigationBar: _BottomBar(index: index, onSelect: (i) => mainTab.value = i),
      ),
    );
  }
}

class _BottomBar extends StatelessWidget {
  const _BottomBar({required this.index, required this.onSelect});

  final int index;
  final ValueChanged<int> onSelect;

  @override
  Widget build(BuildContext context) {
    return Container(
      color: AppColors.background,
      child: SafeArea(
        top: false,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const AppDivider(),
            SizedBox(
              height: 60,
              child: Row(
                children: [
                  for (var i = 0; i < MainScreen._tabs.length; i++)
                    Expanded(
                      child: Semantics(
                        selected: i == index,
                        button: true,
                        child: GestureDetector(
                          behavior: HitTestBehavior.opaque,
                          onTap: () => onSelect(i),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Icon(MainScreen._tabs[i].$1,
                                  size: 24, color: i == index ? AppColors.textPrimary : Palette.gray11),
                              const SizedBox(height: 2),
                              Text(MainScreen._tabs[i].$2,
                                  style: AppText.caption2
                                      .copyWith(color: i == index ? AppColors.textPrimary : Palette.gray11)),
                            ],
                          ),
                        ),
                      ),
                    ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
