<template>
    <div>
        <router-link to="/intro" class="cur">
            <div class="flex">
                <img :src="ImagePaths.logo.logoTab" alt="Logo" class="w-65px" />
                <svg xmlns="http://www.w3.org/2000/svg" width="70" height="60" viewBox="0 0 70 60">
                    <text x="34" y="57" font-size="40" fill="#b90449" text-anchor="middle" font-weight="600">boe</text>
                </svg>
            </div>
        </router-link>
    </div>
</template>

<script setup>
import { ImagePaths } from '@/assets/img/imagePaths';
</script>

<style lang="scss" scoped>
</style>