package module.currency
/**
金價資訊
*/
class GoldInfo{
	def sqlDirName = 'goldPrice'
	def doSync(){
		module.processor.ProcessorRunner.runDayByDay{
					startYear Calendar.getInstance().get(Calendar.YEAR)//Integer.valueOf(listing_day.toString()[0..3])//
					startMonth Calendar.getInstance().get(Calendar.MONTH)+1//Integer.valueOf(listing_day.toString()[4..5])
					startday 1//Calendar.getInstance().get(Calendar.DAY_OF_MONTH)-1
					endYear Calendar.getInstance().get(Calendar.YEAR)
					endMonth Calendar.getInstance().get(Calendar.MONTH)+1
					endDay Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
					process{yyyyMmDd->

						if(new File("./${sqlDirName}/${yyyyMmDd}.sql").exists()){
							println yyyyMmDd+' already done!'
						}else{
							sleep(2400)
							def date="${yyyyMmDd[0..3]}-${yyyyMmDd[4..5]}-${yyyyMmDd[6..7]}"
							def api = "https://goldprice.org/gold-price-today/${date}"
							println api
							def doc = module.web.Webget.download{
								url "${api}" 
								decode 'utf-8'
								retry 3
								parsePage true
							}
							if(doc==null){
								println 'no data'
							}else{
								def datagroup = module.parser.Regix.parse{
									pattern ~/<a href="https:\/\/goldprice\.org">Gold Price<\/a><\/td><td class="text-right">(.+)<\/td><td class="text-right" style="color: .+;">(.+)<\/td><td class="text-right" style="color: .+;">(.+)<\/td><\/tr><tr><td><a href="https:\/\/silverprice\.org">Silver Price<\/a>/
									document doc
									groups Arrays.asList(1,2,3)
								}

								print yyyyMmDd+' '+datagroup
								if(datagroup){
									def resultSql = "replace into `daily_gold_price` (`traded_day`,`price_am`) values ('${yyyyMmDd}','${datagroup[0][0]}');"

									module.io.Batch.exec{
										mkdirs "./${sqlDirName}/"
										write "./${sqlDirName}/${yyyyMmDd}.sql",'UTF-8',"${resultSql}"
									}//exec
									print '*'
								}else{
									println 'x'
								}
							}
						}
					}//yymmdd
		}//run
		module.db.SqlExecuter.exec{
		    dir "./${sqlDirName}"
		}
		module.io.Batch.exec{
			clean "./${sqlDirName}"
			delete "./${sqlDirName}"
			info 'import goldPrice done'
		}
		//////////
	}
	def static sync(@DelegatesTo(GoldInfo) Closure block){
	        GoldInfo m = new GoldInfo()
	        block.delegate = m
	        block()
	        m.doSync()
    }
}