package module.taiex

class NtdInfo{
	def sqlDirName = 'usd_ntd'
	def baseUrl = 'https://www.cbc.gov.tw'
	def menuUrl = baseUrl+ '/tw/np-639-1.html'


	def doSync(){
		def menu = module.web.Webget.download{
			url "${menuUrl}" 
			decode 'utf-8'
			parsePage true
		}

		def usdntdGroup = module.parser.Regix.parse{
			pattern ~/<a href="(.+)" title="新臺幣\/美元 銀行間收盤匯率">/
			document menu
			groups Arrays.asList(1)
		}
		def usttwdUrl=baseUrl+usdntdGroup[0][0]

		def firstPage = module.web.Webget.download{
			url "${usttwdUrl}" 
			decode 'utf-8'
			parsePage true
		}

		def lastPageGroup = module.parser.Regix.parse{
			pattern ~/<a title="最後一頁" href="(.+)">/
			document firstPage
			groups Arrays.asList(1)
		} 
		def pageTemplate=baseUrl+'/tw/'+lastPageGroup[0][0][0..8]+'<pageNo>'+lastPageGroup[0][0][-8..-1]
		def lastPageNumber = Integer.parseInt(lastPageGroup[0][0][9..-9])

		for(int i=1;i<=lastPageNumber;i++){
			def resultSql='REPLACE INTO `usd_ntd` (`traded_day`,`USD`,`NTD`) VALUES'
			def currentPageUrl=pageTemplate.replace('<pageNo>',"$i")
			println currentPageUrl
			def currentPage = module.web.Webget.download{
				url "${currentPageUrl}" 
				decode 'utf-8'
				parsePage true
			}

			def currentPageData = module.parser.Regix.parse{
				pattern ~/<td data-th="標題\(日期\)"><span>(.+)<\/span><\/td>\r\n\s+<td data-th="NTD\/USD"><span>(.+)<\/span><\/td>/
				document currentPage
				groups Arrays.asList(1,2)
			} 

			currentPageData.each{data->
				def year=data[0].replaceAll(/^(\d+)(\/)(\d+)(\/)(\d+)$/,'$1');
				def month=data[0].replaceAll(/^(\d+)(\/)(\d+)(\/)(\d+)$/,'$3')
				def day=data[0].replaceAll(/^(\d+)(\/)(\d+)(\/)(\d+)$/,'$5')
				def traded_day=String.format('%04d%02d%02d',Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day))
				def usd = data[1]
				if(resultSql.endsWith('VALUES')){
					resultSql+="\r\n('$traded_day','$usd','1')"
				}else{
					resultSql+="\r\n,('$traded_day','$usd','1')"
				}
			}
			if(resultSql || !resultSql.endsWith('VALUES')){
	    		module.io.Batch.exec{
        			mkdirs "./${sqlDirName}"
        			write "./${sqlDirName}/${i}.tmp",'UTF-8',"${resultSql};"
        			rename "./${sqlDirName}/${i}.tmp","./${sqlDirName}/${i}.sql"
   				}
			}
			print '*'
			sleep(1000)
		}
		module.db.SqlExecuter.execute{
		    dir "./${sqlDirName}"
		}
		module.io.Batch.exec{
			clean "./${sqlDirName}"
			delete "./${sqlDirName}"
			info 'import usd_ntd done'
		} 
	}

	def static sync(@DelegatesTo(NtdInfo) Closure block){
	        NtdInfo m = new NtdInfo()
	        block.delegate = m
	        block()
	        m.doSync()
    }
}