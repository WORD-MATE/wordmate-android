import 'package:flutter/material.dart';

import '../../data/store.dart';
import '../../utils/nav.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/inputs.dart';
import '../../widgets/layout.dart';
import '../../widgets/sheets.dart';
import '../auth/login_screen.dart';
import '../auth/terms_screen.dart';

class _GroupLabel extends StatelessWidget {
  const _GroupLabel(this.text);

  final String text;

  @override
  Widget build(BuildContext context) => Padding(
        padding: const EdgeInsets.fromLTRB(Gap.screen, 20, Gap.screen, 4),
        child: Text(text, style: AppText.caption1Bold.copyWith(color: AppColors.textTertiary)),
      );
}

/// 로그아웃하고 로그인 화면으로 돌아간다.
void logoutToLogin() {
  store.logout();
  mainTab.value = Tabs.home;
  navigatorKey.currentState!.pushAndRemoveUntil(
    MaterialPageRoute(builder: (_) => const LoginScreen()),
    (_) => false,
  );
}

/// 설정 (알림 · 계정 · 약관) — Figma "로그아웃", "탈퇴".
class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppTopBar(title: '설정', onBack: () => Navigator.pop(context)),
      body: ListView(children: [
        const _GroupLabel('알림'),
        MenuRow('알림 설정', onTap: () => push(context, const NotificationSettingsScreen())),
        Gap.h8,
        const ThickDivider(),
        const _GroupLabel('계정'),
        MenuRow('로그인 관리', onTap: () => push(context, const LoginManagementScreen())),
        MenuRow('로그아웃', chevron: false, onTap: () async {
          final ok = await showAppDialog(context,
              title: '로그아웃할까요?',
              description: '다시 로그인하면 학습 기록을 그대로 이어갈 수 있어요.',
              confirmText: '로그아웃');
          if (ok) logoutToLogin();
        }),
        MenuRow('탈퇴하기', titleColor: AppColors.error, chevron: false, onTap: () async {
          final ok = await showAppDialog(context,
              title: '정말 탈퇴할까요?',
              description: '학습 기록, 단어팩, 랭킹 배지가 모두 삭제되고 복구할 수 없어요.',
              confirmText: '탈퇴하기',
              confirmKind: ButtonKind.danger);
          if (ok) logoutToLogin();
        }),
        Gap.h8,
        const ThickDivider(),
        const _GroupLabel('약관'),
        MenuRow('서비스 이용약관', onTap: () => push(context, const TermDetailScreen(term: TermType.service))),
        MenuRow('개인정보 처리방침', onTap: () => push(context, const TermDetailScreen(term: TermType.privacy))),
        const MenuRow('앱 버전', trailingText: '1.0.0', chevron: false),
      ]),
    );
  }
}

/// 알림 설정 (Figma "알림 설정").
class NotificationSettingsScreen extends StatefulWidget {
  const NotificationSettingsScreen({super.key});

  @override
  State<NotificationSettingsScreen> createState() => _NotificationSettingsScreenState();
}

class _NotificationSettingsScreenState extends State<NotificationSettingsScreen> {
  static const _options = [
    ('battle', '대결 초대', '친구가 대결방에 초대하면 알려드려요'),
    ('sentence', '오늘의 문장', '매일 아침 새 문장이 도착하면 알려드려요'),
    ('friend', '친구 요청', '새 친구 요청을 알려드려요'),
    ('reminder', '학습 리마인더', '매일 오후 8시에 학습을 잊지 않게 알려드려요'),
    ('ranking', '랭킹 변동', '주간 랭킹이 마감되면 결과를 알려드려요'),
    ('marketing', '이벤트·혜택 (선택)', '새 단어팩, 학교 대항전 소식을 받아요'),
  ];

  final _on = {for (final (key, _, _) in _options) key: key != 'marketing'};

  @override
  Widget build(BuildContext context) {
    final all = _on.values.every((v) => v);
    return Scaffold(
      appBar: AppTopBar(title: '알림 설정', onBack: () => Navigator.pop(context)),
      body: ListView(children: [
        _ToggleRow('전체 알림', '모든 알림을 한 번에 켜고 끌 수 있어요', all,
            (v) => setState(() => _on.updateAll((_, _) => v))),
        const ThickDivider(),
        for (final (key, title, desc) in _options)
          _ToggleRow(title, desc, _on[key]!, (v) => setState(() => _on[key] = v)),
      ]),
    );
  }
}

class _ToggleRow extends StatelessWidget {
  const _ToggleRow(this.title, this.description, this.value, this.onChanged);

  final String title;
  final String description;
  final bool value;
  final ValueChanged<bool> onChanged;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: Gap.screen, vertical: 14),
      child: Row(children: [
        Expanded(
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(title, style: AppText.body2Bold),
            Text(description, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
          ]),
        ),
        AppSwitch(value: value, onChanged: onChanged),
      ]),
    );
  }
}

class _Device {
  const _Device(this.name, this.detail, this.icon, {this.current = false});

  final String name;
  final String detail;
  final IconData icon;
  final bool current;
}

/// 로그인 관리 (Figma "로그인 관리", "특정 기기 로그아웃", "다른 기기 모두 로그아웃").
class LoginManagementScreen extends StatefulWidget {
  const LoginManagementScreen({super.key});

  @override
  State<LoginManagementScreen> createState() => _LoginManagementScreenState();
}

class _LoginManagementScreenState extends State<LoginManagementScreen> {
  final _devices = [
    const _Device('Android · 이 기기', '지금 사용 중', Icons.phone_android_rounded, current: true),
    const _Device('Galaxy Tab S9', '광주 · 3일 전', Icons.tablet_rounded),
    const _Device('Chrome · Windows', '광주 · 1주 전', Icons.computer_rounded),
  ];

  Future<void> _logoutDevice(_Device d) async {
    final ok = await showAppDialog(context,
        title: '${d.name}에서 로그아웃할까요?',
        description: '해당 기기에서 다시 로그인해야 WORD MATE를 쓸 수 있어요.',
        confirmText: '로그아웃');
    if (!ok || !mounted) return;
    setState(() => _devices.remove(d));
    showToast(context, '로그아웃했어요');
  }

  Future<void> _logoutOthers() async {
    final ok = await showAppDialog(context,
        title: '다른 기기에서 모두 로그아웃할까요?',
        description: '지금 사용 중인 기기를 제외한 모든 기기에서 로그아웃돼요.',
        confirmText: '모두 로그아웃');
    if (ok) setState(() => _devices.removeWhere((d) => !d.current));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppTopBar(title: '로그인 관리', onBack: () => Navigator.pop(context)),
      body: ListView(children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: Gap.screen, vertical: 8),
          child: Text('WORD MATE에 로그인된 기기 ${_devices.length}대',
              style: AppText.label2.copyWith(color: AppColors.textSecondary)),
        ),
        for (final d in _devices)
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: Gap.screen, vertical: 12),
            child: Row(children: [
              Container(
                width: 40,
                height: 40,
                decoration: const BoxDecoration(color: AppColors.surface, borderRadius: AppRadius.md),
                child: Icon(d.icon, color: AppColors.textSecondary, size: 22),
              ),
              Gap.w12,
              Expanded(
                child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Row(children: [
                    Text(d.name, style: AppText.body2Bold),
                    if (d.current) ...[
                      Gap.w4,
                      const Tag('현재', background: AppColors.primary, color: AppColors.onPrimary),
                    ],
                  ]),
                  Text(d.detail, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
                ]),
              ),
              if (!d.current) SmallButton('로그아웃', kind: ButtonKind.secondary, onPressed: () => _logoutDevice(d)),
            ]),
          ),
      ]),
      bottomNavigationBar: SafeArea(
        top: false,
        child: Padding(
          padding: const EdgeInsets.fromLTRB(Gap.screen, 12, Gap.screen, 16),
          child: AppButton('다른 기기 모두 로그아웃',
              kind: ButtonKind.outline, enabled: _devices.length > 1, onPressed: _logoutOthers),
        ),
      ),
    );
  }
}
