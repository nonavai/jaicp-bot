state: q:service_booking
  q!: * @ServiceRequest * @CarBrand::brand? * @Phone::phone? * @FullName::name? *
  q!: * @ServiceRequest * @FullName::name? * @CarBrand::brand? * @Phone::phone? *
  q!: * @ServiceRequest * @Phone::phone? * @FullName::name? * @CarBrand::brand? *
  q!: * @ServiceRequest * @FullName::name? * @Phone::phone? * @CarBrand::brand? *
  q!: * @ServiceRequest * @CarBrand::brand? * @Phone::phone? * @FullName::name? *
  q!: * @ServiceRequest * @Phone::phone? * @CarBrand::brand? * @FullName::name? *
  q!: * @ServiceRequest *
  q!: * (записаться|запись|запиши|записать) * (на|в) * (ТО|техобслуживание|сервис|диагностику) *
  q!: * (нужно|хочу|хотел бы|хотела бы) * (пройти|сделать|записаться на) * (ТО|техобслуживание|сервис) *
  q!: * (время|пора|подошло время) * (для|на) * (ТО|техобслуживания|сервиса) *
  q!: * (автомобиль|машина|тачка) * (нуждается|нуждается в|требует) * (ТО|техобслуживании|сервисе) *
  q!: * (проблема|неисправность|поломка) * (с|у) * (автомобилем|машиной) * (нужен|требуется) * (ремонт|диагностика|ТО) *
