<template>
  <button 
    class="m-btn"
    :tabindex="tabIndex"
    :name="name"
    :data-tip="dataTip"
    :ref="name"
    :style="{ 
        width: width, 
        height: height, 
        backgroundColor: backgroundColor,
        color: color,
        padding: padding,
        margin: margin,
        border: border,
        borderRadius: radius,
    }"
    :class="{
      close: typeBtn === 'close',
      delete: typeBtn === 'delete',
      'disabled-btn': disabled
    }"
    @click="onClick"
  >
    <slot />
  </button>
</template>
<!-- script setup -->
<script setup>
import { ref, } from 'vue'

// Define props
const props = defineProps({
  text: String,
  height: [String, Number],
  width: [String, Number],
  backgroundColor: String,
  color: String,
  padding: String,
  border: {
    type: String,
    default: 'none'
  },
  radius: {
    type: String,
    default: '5px'
  },
  typeBtn: String,
  margin: String,
  disabled: Boolean,
  tabIndex: Number,
  name: String,
  dataTip: String
})

// Emits
const emit = defineEmits(['click'])

// Ref to button element
const buttonRef = ref(null)

// Methods
const onClick = (event) => {
  emit('click', event)
}
</script>

<style lang="scss" scoped>
@use '@/components/common/button/Button.scss';
</style>
