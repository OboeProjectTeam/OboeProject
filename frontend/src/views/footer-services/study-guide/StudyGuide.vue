<template>
  <div class="study-guide-page">
    <div class="study-guide-container">
      <div class="study-guide-header">
        <h1>{{ t('studyGuide.title') }}</h1>
        <p class="last-updated">{{ t('studyGuide.lastUpdated') }} {{ lastUpdated }}</p>
      </div>

      <div class="study-guide-content">
        <section class="guide-section">
          <h2>{{ t('studyGuide.learningPath.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('studyGuide.learningPath.beginner.title') }}</h3>
            <ul>
              <li>{{ t('studyGuide.learningPath.beginner.items.hiraganaKatakana') }}</li>
              <li>{{ t('studyGuide.learningPath.beginner.items.basicGrammar') }}</li>
              <li>{{ t('studyGuide.learningPath.beginner.items.vocabulary') }}</li>
              <li>{{ t('studyGuide.learningPath.beginner.items.basicKanji') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('studyGuide.learningPath.intermediate.title') }}</h3>
            <ul>
              <li>{{ t('studyGuide.learningPath.intermediate.items.intermediateGrammar') }}</li>
              <li>{{ t('studyGuide.learningPath.intermediate.items.expandedVocabulary') }}</li>
              <li>{{ t('studyGuide.learningPath.intermediate.items.intermediateKanji') }}</li>
              <li>{{ t('studyGuide.learningPath.intermediate.items.comprehensionSkills') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('studyGuide.effectiveMethods.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('studyGuide.effectiveMethods.flashcards.title') }}</h3>
            <p>{{ t('studyGuide.effectiveMethods.flashcards.description') }}</p>
            <ul>
              <li>{{ t('studyGuide.effectiveMethods.flashcards.features.systematic') }}</li>
              <li>{{ t('studyGuide.effectiveMethods.flashcards.features.spacedRepetition') }}</li>
              <li>{{ t('studyGuide.effectiveMethods.flashcards.features.progress') }}</li>
              <li>{{ t('studyGuide.effectiveMethods.flashcards.features.customization') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('studyGuide.effectiveMethods.quizzes.title') }}</h3>
            <p>{{ t('studyGuide.effectiveMethods.quizzes.description') }}</p>
            <ul>
              <li>{{ t('studyGuide.effectiveMethods.quizzes.types.vocabulary') }}</li>
              <li>{{ t('studyGuide.effectiveMethods.quizzes.types.grammar') }}</li>
              <li>{{ t('studyGuide.effectiveMethods.quizzes.types.reading') }}</li>
              <li>{{ t('studyGuide.effectiveMethods.quizzes.types.listening') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('studyGuide.learningResources.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('studyGuide.learningResources.materials.title') }}</h3>
            <ul>
              <li>{{ t('studyGuide.learningResources.materials.items.basicTextbooks') }}</li>
              <li>{{ t('studyGuide.learningResources.materials.items.grammarExercises') }}</li>
              <li>{{ t('studyGuide.learningResources.materials.items.jlptMaterials') }}</li>
              <li>{{ t('studyGuide.learningResources.materials.items.conversationMaterials') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('studyGuide.learningResources.tools.title') }}</h3>
            <ul>
              <li>{{ t('studyGuide.learningResources.tools.items.dictionary') }}</li>
              <li>{{ t('studyGuide.learningResources.tools.items.kanjiLookup') }}</li>
              <li>{{ t('studyGuide.learningResources.tools.items.pronunciation') }}</li>
              <li>{{ t('studyGuide.learningResources.tools.items.mobileApp') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('studyGuide.beginnerTips.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('studyGuide.beginnerTips.goalSetting.title') }}</h3>
            <ul>
              <li>{{ t('studyGuide.beginnerTips.goalSetting.tips.clearGoals') }}</li>
              <li>{{ t('studyGuide.beginnerTips.goalSetting.tips.studyPlan') }}</li>
              <li>{{ t('studyGuide.beginnerTips.goalSetting.tips.shortLongTerm') }}</li>
              <li>{{ t('studyGuide.beginnerTips.goalSetting.tips.trackProgress') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('studyGuide.beginnerTips.motivation.title') }}</h3>
            <ul>
              <li>{{ t('studyGuide.beginnerTips.motivation.tips.dailyStudy') }}</li>
              <li>{{ t('studyGuide.beginnerTips.motivation.tips.community') }}</li>
              <li>{{ t('studyGuide.beginnerTips.motivation.tips.nativePractice') }}</li>
              <li>{{ t('studyGuide.beginnerTips.motivation.tips.entertainment') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('studyGuide.oboeSupport.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('studyGuide.oboeSupport.learningSupport.title') }}</h3>
            <ul>
              <li>{{ t('studyGuide.oboeSupport.learningSupport.services.personalConsultation') }}</li>
              <li>{{ t('studyGuide.oboeSupport.learningSupport.services.expertHelp') }}</li>
              <li>{{ t('studyGuide.oboeSupport.learningSupport.services.materialUpdates') }}</li>
              <li>{{ t('studyGuide.oboeSupport.learningSupport.services.progressReports') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('studyGuide.oboeSupport.community.title') }}</h3>
            <ul>
              <li>{{ t('studyGuide.oboeSupport.community.features.forum') }}</li>
              <li>{{ t('studyGuide.oboeSupport.community.features.studyGroups') }}</li>
              <li>{{ t('studyGuide.oboeSupport.community.features.experienceSharing') }}</li>
              <li>{{ t('studyGuide.oboeSupport.community.features.networking') }}</li>
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
@use '@/views/footer-services/study-guide/StudyGuide.scss';
</style>