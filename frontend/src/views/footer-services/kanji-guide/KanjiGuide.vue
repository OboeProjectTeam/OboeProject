<template>
  <div class="kanji-guide-page">
    <div class="kanji-guide-container">
      <div class="kanji-guide-header">
        <h1>{{ t('kanjiGuide.title') }}</h1>
        <p class="last-updated">{{ t('kanjiGuide.lastUpdated') }} {{ lastUpdated }}</p>
      </div>

      <div class="kanji-guide-content">
        <section class="guide-section">
          <h2>{{ t('kanjiGuide.section1Title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('kanjiGuide.section1_1Title') }}</h3>
            <p>{{ t('kanjiGuide.section1_1Description') }}</p>
            <div class="kanji-example-box">
              <div class="kanji-item">
                <p class="kanji">氵</p>
                <p class="meaning">{{ t('kanjiGuide.waterRadical') }}</p>
                <p class="examples">{{ t('kanjiGuide.waterExamples') }}</p>
              </div>
              <div class="kanji-item">
                <p class="kanji">木</p>
                <p class="meaning">{{ t('kanjiGuide.woodRadical') }}</p>
                <p class="examples">{{ t('kanjiGuide.woodExamples') }}</p>
              </div>
              <div class="kanji-item">
                <p class="kanji">火</p>
                <p class="meaning">{{ t('kanjiGuide.fireRadical') }}</p>
                <p class="examples">{{ t('kanjiGuide.fireExamples') }}</p>
              </div>
            </div>
          </div>
          <div class="sub-section">
            <h3>{{ t('kanjiGuide.section1_2Title') }}</h3>
            <ul>
              <li>{{ t('kanjiGuide.horizontalCombination') }}</li>
              <li>{{ t('kanjiGuide.verticalCombination') }}</li>
              <li>{{ t('kanjiGuide.surroundingCombination') }}</li>
              <li>{{ t('kanjiGuide.complexCombination') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('kanjiGuide.section2Title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('kanjiGuide.section2_1Title') }}</h3>
            <ul>
              <li>{{ t('kanjiGuide.leftToRight') }}</li>
              <li>{{ t('kanjiGuide.topToBottom') }}</li>
              <li>{{ t('kanjiGuide.outsideToInside') }}</li>
              <li>{{ t('kanjiGuide.horizontalBeforeVertical') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('kanjiGuide.section2_2Title') }}</h3>
            <div class="stroke-order-box">
              <div class="stroke-example">
                <p class="kanji">木</p>
                <ol>
                  <li>{{ t('kanjiGuide.woodStroke1') }}</li>
                  <li>{{ t('kanjiGuide.woodStroke2') }}</li>
                  <li>{{ t('kanjiGuide.woodStroke3') }}</li>
                  <li>{{ t('kanjiGuide.woodStroke4') }}</li>
                </ol>
              </div>
              <div class="stroke-example">
                <p class="kanji">水</p>
                <ol>
                  <li>{{ t('kanjiGuide.waterStroke1') }}</li>
                  <li>{{ t('kanjiGuide.waterStroke2') }}</li>
                  <li>{{ t('kanjiGuide.waterStroke3') }}</li>
                  <li>{{ t('kanjiGuide.waterStroke4') }}</li>
                  <li>{{ t('kanjiGuide.waterStroke5') }}</li>
                </ol>
              </div>
            </div>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('kanjiGuide.section3Title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('kanjiGuide.section3_1Title') }}</h3>
            <p>{{ t('kanjiGuide.section3_1Description') }}</p>
            <div class="reading-examples">
              <div class="reading-example">
                <span class="kanji">水</span>
                <span class="reading">{{ t('kanjiGuide.waterVietnamese') }}</span>
              </div>
              <div class="reading-example">
                <span class="kanji">木</span>
                <span class="reading">{{ t('kanjiGuide.woodVietnamese') }}</span>
              </div>
              <div class="reading-example">
                <span class="kanji">火</span>
                <span class="reading">{{ t('kanjiGuide.fireVietnamese') }}</span>
              </div>
            </div>
          </div>
          <div class="sub-section">
            <h3>{{ t('kanjiGuide.section3_2Title') }}</h3>
            <p>{{ t('kanjiGuide.section3_2Description') }}</p>
            <ul>
              <li><strong>{{ t('kanjiGuide.onReading') }}:</strong> {{ t('kanjiGuide.onReadingDesc') }}</li>
              <li><strong>{{ t('kanjiGuide.kunReading') }}:</strong> {{ t('kanjiGuide.kunReadingDesc') }}</li>
            </ul>
            <div class="reading-examples">
              <div class="reading-example">
                <span class="kanji">水</span>
                <span class="reading">{{ t('kanjiGuide.waterJapanese') }}</span>
              </div>
              <div class="reading-example">
                <span class="kanji">木</span>
                <span class="reading">{{ t('kanjiGuide.woodJapanese') }}</span>
              </div>
            </div>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('kanjiGuide.section4Title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('kanjiGuide.section4_1Title') }}</h3>
            <ul>
              <li>{{ t('kanjiGuide.section4_1Method1') }}</li>
              <li>{{ t('kanjiGuide.section4_1Method2') }}</li>
              <li>{{ t('kanjiGuide.section4_1Method3') }}</li>
              <li>{{ t('kanjiGuide.section4_1Method4') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('kanjiGuide.section4_2Title') }}</h3>
            <ul>
              <li>{{ t('kanjiGuide.section4_2Method1') }}</li>
              <li>{{ t('kanjiGuide.section4_2Method2') }}</li>
              <li>{{ t('kanjiGuide.section4_2Method3') }}</li>
              <li>{{ t('kanjiGuide.section4_2Method4') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('kanjiGuide.section5Title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('kanjiGuide.section5_1Title') }}</h3>
            <div class="tips-box">
              <div class="tip-item">
                <h4>{{ t('kanjiGuide.tip1Title') }}</h4>
                <p>{{ t('kanjiGuide.tip1Description') }}</p>
              </div>
              <div class="tip-item">
                <h4>{{ t('kanjiGuide.tip2Title') }}</h4>
                <p>{{ t('kanjiGuide.tip2Description') }}</p>
              </div>
              <div class="tip-item">
                <h4>{{ t('kanjiGuide.tip3Title') }}</h4>
                <p>{{ t('kanjiGuide.tip3Description') }}</p>
              </div>
            </div>
          </div>
          <div class="sub-section">
            <h3>{{ t('kanjiGuide.section5_2Title') }}</h3>
            <ul>
              <li>{{ t('kanjiGuide.tool1') }}</li>
              <li>{{ t('kanjiGuide.tool2') }}</li>
              <li>{{ t('kanjiGuide.tool3') }}</li>
              <li>{{ t('kanjiGuide.tool4') }}</li>
            </ul>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const lastUpdated = ref('15/03/2024');
</script>

<style lang="scss" scoped>
@use '@/views/footer-services/kanji-guide/KanjiGuide.scss';
</style>