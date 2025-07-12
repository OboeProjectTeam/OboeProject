<template>
    <div>
        <router-link to="/intro" class="cur">
            <div class="flex">
                <img :src="ImagePaths.logo.logoTab" alt="Logo" :style="{ width: imgWidth + 'px' }" />
                <svg xmlns="http://www.w3.org/2000/svg" :width="svgWidth" :height="svgHeight" viewBox="0 0 70 60">
                    <text :x="textX" :y="textY" :font-size="fontSize" fill="#b90449" text-anchor="middle" font-weight="600">boe</text>
                </svg>
            </div>
        </router-link>
    </div>
</template>

<script setup>
import { ImagePaths } from '@/assets/img/imagePaths';

defineProps({
    imgWidth: {
        type: Number,
        default: 65
    },
    svgWidth: {
        type: Number,
        default: 70
    },
    svgHeight: {
        type: Number,
        default: 60
    },
    fontSize: {
        type: Number,
        default: 40
    },
    textX: {
        type: Number,
        default: 34
    },
    textY: {
        type: Number,
        default: 57
    }
});
</script>

<style lang="scss" scoped>
.flex {
    display: flex;
    align-items: center;
    gap: 2px;
}

.cur {
    cursor: pointer;
    text-decoration: none;
}
</style>