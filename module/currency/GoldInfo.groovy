package module.currency
/**
金價資訊
*/
class GoldInfo{
	def sqlDirName = 'goldPrice'
	def doSync(){
		// def mapping = [January:'01',
		// 				February:'02',
		// 				March:'03',
		// 				April:'04',
		// 				May:'05',
		// 				June:'06',
		// 				July:'07',
		// 				August:'08',
		// 				September:'09',
		// 				October:'10',
		// 				November:'11',
		// 				December:'12'
		// 				]
		// //https://sdbullion.com/gold-prices-1968
		// new File("./data/goldprice_daily.txt").eachLine { line ->
		// 	if(line.trim()==''){
		// 		//next
		// 	}else{
		// 		if(!line.trim().startsWith('$')){
		// 			def data=line.replaceAll(',','').replace('$','').replaceAll(' oz','')
		// 			def datas=data.split(' ')
		// 			// println datas
		// 			print '\r\n'+datas[2]+''+mapping."${datas[0]}"+''+String.format('%02d',Integer.valueOf(datas[1]))+''
		// 			File csv = new File('gold.txt')
  //               	csv.append('\r\n'+datas[2]+''+mapping."${datas[0]}"+''+String.format('%02d',Integer.valueOf(datas[1]))+'')
		// 		}else{
		// 			def data=line.replaceAll(',','').replace('$','').replaceAll(' oz','')
		// 			print ','+data
		// 			File csv = new File('gold.txt')
		// 			csv.append(','+data)
		// 		}
			
		// 	}
		// }
		// new File("./data/2018.txt").eachLine { line ->
		// 	if(line.trim()==''){
		// 		//next
		// 	}else{
		// 		if(!line.trim().startsWith('$')){
		// 			def data=line.replaceAll(',','').replace('$','').replaceAll(' oz','')
		// 			def datas=data.split('/')
		// 			// println datas
		// 			print '\r\n'+datas[2]+''+String.format('%02d',Integer.valueOf(datas[0]))+''+String.format('%02d',Integer.valueOf(datas[1]))+','
		// 			File csv = new File('gold.txt')
		// 			csv.append('\r\n'+datas[2]+''+String.format('%02d',Integer.valueOf(datas[0]))+''+String.format('%02d',Integer.valueOf(datas[1]))+',')
		// 		}else{
		// 			def data=line.replaceAll(',','').replace('$','').replaceAll(' oz','')
		// 			print ''+data+','
		// 			File csv = new File('gold.txt')
		// 			csv.append(''+data+',')
		// 		}
			
		// 	}
		// }
		// new File("./data/gold.csv").eachLine { line ->
		// 	def data= line.split(',')
		// 	if(data.size()==1){

		// 	}else if(data.size()==2){
		// 		println 'INSERT LOW_PRIORITY INTO `findb`.`daily_gold_price` (`traded_day`, `price_am`) VALUES ('+"\'${data.join('\',\'')}\');"
		// 	}else if(data.size()==3){
		// 		println 'INSERT LOW_PRIORITY INTO `findb`.`daily_gold_price` (`traded_day`, `price_am`,`price_pm`) VALUES ('+"\'${data.join('\',\'')}\');"
		// 	}
		// }
		// for(int i=1;i<=31;i++){
		// 	String day=String.format('%02d',Integer.valueOf(i))
		// 	def date="2019-01-${day}"
		// 	println date
		// }
		/////////

		module.processor.ProcessorRunner.runDayByDay{
					startYear Calendar.getInstance().get(Calendar.YEAR)//Integer.valueOf(listing_day.toString()[0..3])//
					startMonth Calendar.getInstance().get(Calendar.MONTH)+1//Integer.valueOf(listing_day.toString()[4..5])
					startday Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
					endYear Calendar.getInstance().get(Calendar.YEAR)
					endMonth Calendar.getInstance().get(Calendar.MONTH)+1
					endDay Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
					process{yyyyMmDd->

						if(new File("./${sqlDirName}/${yyyyMmDd}.sql").exists()){
							println yyyyMmDd+' already done!'
						}else{
							sleep(1000)
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