<template>
  <div class="writing-guide-page">
    <div class="writing-guide-container">
      <div class="writing-guide-header">
        <h1>{{ t('writingGuide.title') }}</h1>
        <p class="last-updated">{{ t('writingGuide.lastUpdated') }} {{ lastUpdated }}</p>
      </div>

      <div class="writing-guide-content">
        <section class="guide-section">
          <h2>{{ t('writingGuide.overview.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('writingGuide.overview.writingSystems.title') }}</h3>
            <p>{{ t('writingGuide.overview.writingSystems.description') }}</p>
            <ul>
              <li>{{ t('writingGuide.overview.writingSystems.systems.hiragana') }}</li>
              <li>{{ t('writingGuide.overview.writingSystems.systems.katakana') }}</li>
              <li>{{ t('writingGuide.overview.writingSystems.systems.kanji') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('writingGuide.overview.roles.title') }}</h3>
            <ul>
              <li>{{ t('writingGuide.overview.roles.hiragana') }}</li>
              <li>{{ t('writingGuide.overview.roles.katakana') }}</li>
              <li>{{ t('writingGuide.overview.roles.kanji') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('writingGuide.hiragana.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('writingGuide.hiragana.characteristics.title') }}</h3>
            <ul>
              <li>{{ t('writingGuide.hiragana.characteristics.features.curved') }}</li>
              <li>{{ t('writingGuide.hiragana.characteristics.features.basic') }}</li>
              <li>{{ t('writingGuide.hiragana.characteristics.features.diacritics') }}</li>
              <li>{{ t('writingGuide.hiragana.characteristics.features.strokeOrder') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('writingGuide.hiragana.usage.title') }}</h3>
            <ul>
              <li>{{ t('writingGuide.hiragana.usage.uses.nativeWords') }}</li>
              <li>{{ t('writingGuide.hiragana.usage.uses.verbEndings') }}</li>
              <li>{{ t('writingGuide.hiragana.usage.uses.particles') }}</li>
              <li>{{ t('writingGuide.hiragana.usage.uses.furigana') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('writingGuide.katakana.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('writingGuide.katakana.characteristics.title') }}</h3>
            <ul>
              <li>{{ t('writingGuide.katakana.characteristics.features.angular') }}</li>
              <li>{{ t('writingGuide.katakana.characteristics.features.corresponding') }}</li>
              <li>{{ t('writingGuide.katakana.characteristics.features.recognizable') }}</li>
              <li>{{ t('writingGuide.katakana.characteristics.features.longVowels') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('writingGuide.katakana.usage.title') }}</h3>
            <ul>
              <li>{{ t('writingGuide.katakana.usage.uses.foreignWords') }}</li>
              <li>{{ t('writingGuide.katakana.usage.uses.foreignNames') }}</li>
              <li>{{ t('writingGuide.katakana.usage.uses.technical') }}</li>
              <li>{{ t('writingGuide.katakana.usage.uses.emphasis') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('writingGuide.distinction.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('writingGuide.distinction.appearance.title') }}</h3>
            <div class="comparison-table">
              <div class="table-row header">
                <div class="cell">{{ t('writingGuide.distinction.appearance.tableHeaders.sound') }}</div>
                <div class="cell">{{ t('writingGuide.distinction.appearance.tableHeaders.hiragana') }}</div>
                <div class="cell">{{ t('writingGuide.distinction.appearance.tableHeaders.katakana') }}</div>
              </div>
              <div class="table-row">
                <div class="cell">A</div>
                <div class="cell">あ</div>
                <div class="cell">ア</div>
              </div>
              <div class="table-row">
                <div class="cell">I</div>
                <div class="cell">い</div>
                <div class="cell">イ</div>
              </div>
              <div class="table-row">
                <div class="cell">U</div>
                <div class="cell">う</div>
                <div class="cell">ウ</div>
              </div>
              <div class="table-row">
                <div class="cell">E</div>
                <div class="cell">え</div>
                <div class="cell">エ</div>
              </div>
              <div class="table-row">
                <div class="cell">O</div>
                <div class="cell">お</div>
                <div class="cell">オ</div>
              </div>
            </div>
          </div>
          <div class="sub-section">
            <h3>{{ t('writingGuide.distinction.context.title') }}</h3>
            <div class="example-box">
              <p class="example-title">{{ t('writingGuide.distinction.context.example.title') }}</p>
              <p class="example-text">{{ t('writingGuide.distinction.context.example.sentence') }}</p>
              <p class="example-explain" v-html="t('writingGuide.distinction.context.example.explanation')"></p>
            </div>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('writingGuide.learningMethods.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('writingGuide.learningMethods.studyOrder.title') }}</h3>
            <ol>
              <li>{{ t('writingGuide.learningMethods.studyOrder.steps.hiraganaFirst') }}</li>
              <li>{{ t('writingGuide.learningMethods.studyOrder.steps.katakanaSecond') }}</li>
              <li>{{ t('writingGuide.learningMethods.studyOrder.steps.combinedReading') }}</li>
              <li>{{ t('writingGuide.learningMethods.studyOrder.steps.kanjiParallel') }}</li>
            </ol>
          </div>
          <div class="sub-section">
            <h3>{{ t('writingGuide.learningMethods.memoryTechniques.title') }}</h3>
            <ul>
              <li>{{ t('writingGuide.learningMethods.memoryTechniques.techniques.flashcards') }}</li>
              <li>{{ t('writingGuide.learningMethods.memoryTechniques.techniques.handwriting') }}</li>
              <li>{{ t('writingGuide.learningMethods.memoryTechniques.techniques.realTexts') }}</li>
              <li>{{ t('writingGuide.learningMethods.memoryTechniques.techniques.strokeOrder') }}</li>
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
const lastUpdated = ref('15/12/2024');
</script>

<style lang="scss" scoped>
@use '@/views/footer-services/writing-guide/WritingGuide.scss';
</style>