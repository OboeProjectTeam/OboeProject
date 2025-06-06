<template>
  <div class="m-radio-base">
    <input
      type="radio"
      :id="id"
      :name="nameInput"
      :checked="checked"
      @change="onChange"
      class="m-radio-button"
      :tabindex="tabIndex"
    />
    <label
      :for="id"
      :style="{ margin }"
    >
      {{ textField }}
    </label>
  </div>
</template>

<script setup>

// Props
const props = defineProps({
  textField: String,
  margin: String,
  nameInput: String,
  id: String,
  checked: Boolean,
  tabIndex: Number
})

// Emit
const emit = defineEmits(['change'])

// Emit khi radio thay đổi
const onChange = () => {
  emit('change', props.id)
}
</script>

<style lang="scss" scoped>
@use '@/components/common/radio-button/RadioButton.scss';
</style>
