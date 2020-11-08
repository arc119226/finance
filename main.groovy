class Main{
	static boolean done = false
	static main(args) {
	System.setProperty("http.agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
	// module.taiex.StockList.sync{} //日跑
	// module.taiex.CompanyGroup.sync{}
	// module.taiex.ConceptStock.sync{}
	// module.currency.NtdInfo.sync{} //日跑
	// module.currency.BitcoinInfo.sync{}//日跑
	// module.currency.GoldInfo.sync{} //日跑
		Runtime.getRuntime().addShutdownHook(new Thread() {
	            public void run() {
	            	if(done==false){
	                	"cmd /c start /min groovy Main.groovy & close.bat".execute()
	                }
	            }
	        });	
	 module.taiex.StockDay.sync{} //日跑
	 done = true
	 module.taiex.NexResult.sync{}//日跑
	module.taiex.PeDyPb.sync{} //日跑
	module.taiex.Investors.sync{} //日跑
	 module.taiex.HighlightsOfDailyTrading.sync{} //日跑
	module.taiex.ForeignMainlandAreaInvestorsTradingAndShareholding.sync{} //日跑 晚上十點以後
	module.taiex.DailyForeignShareholdingByIndustrial.sync{} //日跑 晚上十點以後
	module.taiex.MarginTransactionl.sync{} //日跑 晚上十點以後
	module.taiex.ShortSalesVolumeAndValue.sync{}//日跑 晚上十點以後	
	 module.taiex.Taiex.sync{} //日跑

	 module.taiex.InvestorsRank.sync{}//日跑


	// module.taiex.YearlyTradingSummary.sync{} //年底跑
	// module.taiex.MonthlyClosingAveragePrice.sync{} //月初跑
	// module.taiex.MonthlyTradingSummary.sync{}//月初跑

	}
}