<script setup lang="ts">
import type { Payment } from '@/components/datatable/column'
import { columns, payments } from '@/components/datatable/column'
import DataTable from '@/components/datatable/DataTable.vue'
import { onMounted, ref } from 'vue'

const data = ref<Payment[]>([])

async function getData(): Promise<Payment[]> {
  // Fetch data from your API here.
  return payments
}

onMounted(async () => {
  data.value = await getData()
})
</script>

<template>
  <div class="container py-10 mx-auto">
    <DataTable :columns="columns" :data="data" />
  </div>
</template>
