<template>
    <div>
       Intro Init
    </div>
</template>

<script>
import { reactive, toRefs } from 'vue'

export default {
    name: 'Intro',
    setup () {
        const state = reactive({
            count: 0,
        })
    
        return {
            ...toRefs(state),
        }
    }
}
</script>

<style lang="scss" scoped>

</style>