<template>
  <div class="ms-textarea-main">
    <label
      v-if="!noTitle"
      :for="name"
      class="lable-textarea"
      :style="{ fontSize: fontSizeTile + 'px' }"
    >
      {{ title }} <span class="required">{{ required ? '*' : '' }}</span>
    </label><br />

    <textarea
      :type="type"
      :name="name"
      :tabindex="tabIndex"
      :placeholder="placeholder"
      :value="modelValue"
      @click="onClick"
      @blur="onBlur"
      @input="changeValue"
      class="ms-textarea"
      :style="{
        width: widthtext + 'px',
        height: heighttext + 'px',
        marginTop: marginToptextarea + 'px'
      }"
      :rows="Rows"
      :cols="Cols"
    ></textarea>
  </div>
</template>

<script setup>

const props = defineProps({
  Cols: String,
  Rows: String,
  noTitle: Boolean,
  title: String,
  tabIndex: Number,
  name: String,
  type: {
    type: String,
    default: 'text'
  },
  placeholder: String,
  modelValue: String,
  required: Boolean,
  heighttext: Number,
  widthtext: Number,
  marginToptextarea: Number,
  fontSizeTile: Number
})

const emit = defineEmits(['click', 'blur', 'update:modelValue', 'message-error-input'])

const onClick = (e) => {
  emit('click', e)
}

const onBlur = (e) => {
  emit('blur', e)
}

const changeValue = (event) => {
  emit('update:modelValue', event.target.value)
  emit('message-error-input', props.name, '')
}
</script>

<style lang="scss" scoped>
@use '@/components/common/textarea/Textarea.scss';
</style>
