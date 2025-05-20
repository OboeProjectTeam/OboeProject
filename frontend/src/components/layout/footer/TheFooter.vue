<template>
  <div class="footer">
    <div class="footer-bot flex">
      <div class="footer-infor">
        <TheLogo class="footer-logo" />
        <ul>
          <li class="flex p-hover" v-for="(item, index) in listInfor" :key="index">
            <div class="mr-10 pb-25 footer-inforicon">
              <i :class="item.icon" class="w-100"></i>
            </div>
            <p class="footer-infortext">{{ item.title }}</p>
          </li>
        </ul>
        <ul class="flex">
          <li class="mr-10" v-for="(item, index) in listCNC" :key="index">
            <a :href="item.src">
              <i :class="item.icon" class="w-100"></i>
            </a>
          </li>
        </ul>
      </div>
      <div class="footer-listservies-hr">
        <ul class="flex">
          <li class="mr-10 listservies-box" v-for="(items, outerIndex) in listServies" :key="outerIndex">
            <p class="listservies-title">{{ items.title }}</p>
            <div class="listservies-hr"></div>
            <ul>
              <li :class="[
                'listservies-detail',
                {
                  cur: outerIndex !== listServies.length - 1,
                  'p-hover': outerIndex !== listServies.length - 1
                }
              ]" v-for="(item, innerIndex) in items.service" :key="innerIndex">
                <a :href="item.link || '#'">
                  <p>{{ item.title }}</p>
                </a>
              </li>
              <MsButton class="button-hover" border="1px solid #000" width="100%" height="40px" backgroundColor="#ffff"
                color="black" v-if="outerIndex === listServies.length - 1">
                SEND COMMENTS
              </MsButton>
            </ul>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useStore } from 'vuex'; // Import useStore to access Vuex store
import TheLogo from '../../logo/TheLogo.vue';
import MsButton from '../../button/MsButton.vue';

const store = useStore(); // Get Vuex store

// Fetch the data from Vuex store
const listInfor = store.getters['footer/ListInfor'];
const listCNC = store.getters['footer/ListCNC'];
const listServies = store.getters['footer/ListServies'];
</script>

<style lang="scss" scoped>
@import url(./TheFooterCss.scss);
</style>
