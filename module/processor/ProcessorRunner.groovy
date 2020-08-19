package module.processor
class ProcessorRunner{

	Integer startYear=2010, startMonth=1, startday=1, endYear=2010, endMonth=1, endDay=1
	def process

	def startYear(def startYear){
		this.startYear=startYear
	}
	def startMonth(def startMonth){
		this.startMonth=startMonth
	}
	def startday(def startday){
		this.startday=startday
	}
	def endYear(def endYear){
		this.endYear=endYear
	}
	def endMonth(def endMonth){
		this.endMonth=endMonth
	}
	def endDay(def endDay){
		this.endDay=endDay
	}

	def process(def process){
		this.process=process
	}
	def doMonthByMonth(){
		def startYyyyMmDd = String.format("%04d%02d%02d",startYear,startMonth,1)
		def endYyyyMmDd = String.format("%04d%02d%02d",endYear,endMonth,endDay)
		for(int year = startYear; year <= endYear; year++){
			for(int month = 1;month <= 12; month++){
				def currentYyyyMmDd = String.format("%04d%02d%02d",year,month,1)
				if(Integer.valueOf(startYyyyMmDd) <= Integer.valueOf(currentYyyyMmDd) &&
					Integer.valueOf(endYyyyMmDd) >= Integer.valueOf(currentYyyyMmDd)){
					process.call(currentYyyyMmDd)
				}
				if(Integer.valueOf(endYyyyMmDd) < Integer.valueOf(currentYyyyMmDd)){
					return;
				}
			}
		}
	}
	def doProcessDayByDay(){
		def startYyyyMmDd = String.format("%04d%02d%02d",startYear,startMonth,startday)
		def endYyyyMmDd = String.format("%04d%02d%02d",endYear,endMonth,endDay)
		for(int year = startYear; year <= endYear; year++){
			for(int month = 1;month <= 12; month++){
				for(int day = 1;day <= 31;day++){
					def currentYyyyMmDd = String.format("%04d%02d%02d",year,month,day)
					if(Integer.valueOf(startYyyyMmDd) <= Integer.valueOf(currentYyyyMmDd) &&
						Integer.valueOf(endYyyyMmDd) >= Integer.valueOf(currentYyyyMmDd)){
							process.call(currentYyyyMmDd)
					}
					if(Integer.valueOf(endYyyyMmDd) < Integer.valueOf(currentYyyyMmDd)){
						return;
					}
				}
			}
		}
	}

	def static runDayByDay(@DelegatesTo(ProcessorRunner) Closure block){
        ProcessorRunner m = new ProcessorRunner()
        block.delegate = m
        block()
        return m.doProcessDayByDay()
    }
    def static runMonthByMonth(@DelegatesTo(ProcessorRunner) Closure block){
		ProcessorRunner m = new ProcessorRunner()
        block.delegate = m
        block()
        return m.doMonthByMonth()
    }
}


