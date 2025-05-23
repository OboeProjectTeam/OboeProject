<template>
    <div class="min-h-screen flex items-center justify-center bg-gray-100">
        <h1 class="text-4xl font-bold text-blue-600">✅ Tailwind is working!</h1>
    </div>
</template>

<script>
import { reactive, toRefs } from 'vue'

export default {
    name: 'Intro',
    setup() {
        const state = reactive({
            count: 0,
        })

        return {
            ...toRefs(state),
        }
    }
}
</script>

<style lang="scss" scoped></style>