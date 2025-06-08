<template>
  <label class="smart-checkbox">
    <input type="checkbox" :checked="modelValue" @change="$emit('update:modelValue', $event.target.checked)"
      :style="{ width: size + 'px', height: size + 'px' }" />
    <span class="checkmark"></span>
    <slot />
  </label>
</template>

<script setup>
defineProps({
  modelValue: Boolean,
  size: {
    type: Number,
    default: 20 // Kích thước mặc định là 20px
  }
})
defineEmits(['update:modelValue'])
</script>

<style lang="scss" scoped>
@use '@/components/common/checkbox/CheckBox.scss';
</style>
