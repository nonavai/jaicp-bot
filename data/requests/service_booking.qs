state: q:service_booking
  q!: * @ServiceRequest *
  q!: * (хочу записаться на ТО|запиши меня на техобслуживание|подошло время ТО|пройти ТО) *
  q!: * @ServiceRequest * @FullName::name? * @Phone::phone? * @CarBrand::brand?
  q!: * @FullName::name? * @Phone::phone? * @CarBrand::brand? * @ServiceRequest *
  q!: * (записать|запись|нужно ТО|техобслуживание) *
  q!: * (диагностика|ремонт|сервис) *