class Main{
	static main(args) {
		// Runtime.getRuntime().addShutdownHook(new Thread() {
	 //            public void run() {
	 //                "cmd /c start /min groovy Main.groovy & close.bat".execute()

	 //            }
	 //        });
	// module.taiex.StockList.sync{} //日跑
	 // module.taiex.StockDay.sync{} //日跑
	// module.taiex.PeDyPb.sync{} //日跑
	// module.taiex.Investors.sync{} //日跑
	// module.taiex.CompanyGroup.sync{}
	// module.taiex.ConceptStock.sync{}
	// module.taiex.YearlyTradingSummary.sync{} //年底跑
	// module.taiex.MonthlyClosingAveragePrice.sync{} //月底跑
	// module.taiex.MonthlyTradingSummary.sync{}//月初跑
	 // module.taiex.HighlightsOfDailyTrading.sync{} //日跑
	// module.taiex.ForeignMainlandAreaInvestorsTradingAndShareholding.sync{} //日跑 晚上十點以後
	// module.taiex.DailyForeignShareholdingByIndustrial.sync{} //日跑 晚上十點以後
	// module.taiex.MarginTransactionl.sync{} //日跑 晚上十點以後
	// module.taiex.ShortSalesVolumeAndValue.sync{}//日跑 晚上十點以後
	 // module.taiex.Taiex.sync{} //日跑
	 //module.taiex.StatisticsTradePerMinute.sync{} //可以不用跑
	 // module.currency.NtdInfo.sync{} //日跑
	 // module.taiex.NexResult.sync{}//日跑
	 // module.taiex.InvestorsRank.sync{}//日跑
	 // module.currency.BitcoinInfo.sync{}//日跑
	 module.currency.GoldInfo.sync{} //日跑
	}
}