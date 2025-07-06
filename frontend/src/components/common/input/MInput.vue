<template>
  <div
    :style="{ width }"
    class="m-input-component"
    :class="{ error: errorMsg }"
  >
    <label for="m-input" class="m-lable">
      {{ textField }}
      <span>{{ required && textField ? " *" : "" }}</span>
    </label>

    <div
      :style="{ height, marginTop: !textField ? '' : marginTopInput }"
      class="m-input-main"
    >
      <input
        class="m-input"
        :type="type"
        id="m-input"
        :ref="name"
        :placeholder="placeholder"
        :value="modelValue"
        :style="{
          paddingRight,
          borderColor: isFocused.value ? focusBorderColor : '',
        }"
        :readonly="isReadonly"
        @focus="onFocus"
        @blur="onBlur"
        @input="changeValue"
        :name="name"
        :tabindex="tabIndex"
      />
      <p class="m-input__error-msg">{{ errorMsg }}</p>
      <div class="m-input-icon">
        <div :class="classIcon" class="m-input-icon-rotate"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, } from 'vue'

// Props
const props = defineProps({
  focusBorderColor: { type: String, default: 'red' },
  textField: String,
  modelValue: String,
  type: String,
  width: String,
  height: String,
  marginBottom: String,
  placeholder: String,
  required: Boolean,
  classIcon: String,
  top: String,
  isReadonly: Boolean,
  originValue: [String, Number],
  name: { type: String, default: '' },
  errorMsg: { type: String, default: '' },
  rules: { type: Array, default: () => [] },
  tabIndex: Number
})

// Emits
const emit = defineEmits([
  'update:modelValue',
  'change-input',
  'message-error-input'
])

// Refs
const isFocused = ref(false)

// Methods
const onFocus = () => {
  isFocused.value = true
}
const onBlur = () => {
  isFocused.value = false
}
const changeValue = (event) => {
  emit('update:modelValue', event.target.value)
  emit('message-error-input', props.name, '')
}
const checkValidate = () => {
  if (props.rules.length > 0) {
    // ⚠  có thể thêm logic gọi validate ở đây
    // emit('message-error-input', props.name, msgErrorInput)
  }
}

// Computed
const paddingRight = computed(() => {
  return props.classIcon ? '36px' : '16px'
})
const marginTopInput = computed(() => {
  return props.textField ? '5px' : ''
})
</script>

<style lang="scss" scoped>
@use '@/components/common/input/Input.scss';
</style>
