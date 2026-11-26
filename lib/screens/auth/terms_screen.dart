import 'package:flutter/material.dart';

import '../../data/store.dart';
import '../../utils/nav.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/inputs.dart';
import '../../widgets/layout.dart';
import '../main_screen.dart';

enum TermType {
  service('서비스 이용약관', true, [
    ('목적', '본 약관은 WORD MATE(이하 "서비스")의 이용 조건과 절차, 이용자와 운영자의 권리·의무를 규정합니다.'),
    ('이용 계약', '이용자는 학교 계정(DataGSM)으로 로그인하고 본 약관에 동의함으로써 서비스를 이용할 수 있습니다.'),
    ('이용자의 의무', '이용자는 대결·단어팩 기능을 이용할 때 다른 이용자에게 불쾌감을 주는 행위를 해서는 안 됩니다.'),
  ]),
  privacy('개인정보 수집·이용 동의', true, [
    ('수집 항목', '이름, 학년·반·번호, 학교 계정 식별자, 학습 및 대결 기록'),
    ('이용 목적', '학습 진도 관리, 랭킹 산정, 대결 매칭 및 서비스 개선'),
    ('보유 기간', '회원 탈퇴 시 지체 없이 파기 (관계 법령에 따라 보관이 필요한 경우 제외)'),
  ]),
  marketing('마케팅 정보 수신 동의', false, [
    ('수신 내용', '새 단어팩 출시, 학교 대항전 등 이벤트 소식'),
    ('수신 방법', '앱 푸시 알림, 학교 이메일'),
    ('철회', '동의하지 않아도 서비스 이용에 제한이 없으며, 설정 > 알림 설정에서 언제든 변경할 수 있습니다.'),
  ]);

  const TermType(this.title, this.required, this.body);
  final String title;
  final bool required;
  final List<(String, String)> body;
}

class TermsScreen extends StatefulWidget {
  const TermsScreen({super.key});

  @override
  State<TermsScreen> createState() => _TermsScreenState();
}

class _TermsScreenState extends State<TermsScreen> {
  final _agreed = <TermType>{};

  bool get _all => _agreed.length == TermType.values.length;
  bool get _requiredOk => TermType.values.where((t) => t.required).every(_agreed.contains);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppTopBar(onBack: () => Navigator.pop(context)),
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Gap.h8,
            Text('${store.user.name}님, 환영해요!\n약관에 동의해 주세요', style: AppText.headline1),
            const SizedBox(height: 32),
            Material(
              color: _all ? AppColors.surfacePressed : AppColors.surface,
              borderRadius: AppRadius.md,
              clipBehavior: Clip.antiAlias,
              child: InkWell(
                onTap: () => setState(() => _all ? _agreed.clear() : _agreed.addAll(TermType.values)),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Row(children: [
                    CheckCircle(checked: _all),
                    Gap.w12,
                    Text('전체 동의하기', style: AppText.body2Bold),
                  ]),
                ),
              ),
            ),
            Gap.h12,
            for (final term in TermType.values)
              InkWell(
                onTap: () => setState(() => _agreed.contains(term) ? _agreed.remove(term) : _agreed.add(term)),
                child: Padding(
                  padding: const EdgeInsets.fromLTRB(16, 8, 0, 8),
                  child: Row(children: [
                    CheckCircle(checked: _agreed.contains(term)),
                    Gap.w12,
                    Expanded(
                      child: Text('[${term.required ? '필수' : '선택'}] ${term.title}',
                          style: AppText.label1.copyWith(color: AppColors.textSecondary)),
                    ),
                    AppIconButton(
                      Icons.chevron_right_rounded,
                      tooltip: '${term.title} 자세히 보기',
                      color: AppColors.textTertiary,
                      onPressed: () => push(context, TermDetailScreen(term: term)),
                    ),
                  ]),
                ),
              ),
          ],
        ),
      ),
      bottomNavigationBar: BottomCta(
        '동의하고 시작하기',
        enabled: _requiredOk,
        onPressed: () => Navigator.of(context).pushAndRemoveUntil(
          MaterialPageRoute(builder: (_) => const MainScreen()),
          (_) => false,
        ),
      ),
    );
  }
}

class TermDetailScreen extends StatelessWidget {
  const TermDetailScreen({super.key, required this.term});

  final TermType term;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppTopBar(onBack: () => Navigator.pop(context)),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
        children: [
          Text(term.title, style: AppText.headline2),
          Gap.h24,
          for (final (heading, text) in term.body) ...[
            Text(heading, style: AppText.label1Bold),
            const SizedBox(height: 6),
            Text(text, style: AppText.label1.copyWith(color: AppColors.textSecondary)),
            Gap.h20,
          ],
        ],
      ),
      bottomNavigationBar: BottomCta('확인', onPressed: () => Navigator.pop(context)),
    );
  }
}
