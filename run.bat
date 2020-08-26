@echo off
set startime=%time%

call groovy 1_stock.groovy
call groovy 2_stock_day.groovy
call groovy 3_pe_dy_pb.groovy
call groovy 4_investors.groovy
call groovy 5_company_group.groovy
call groovy 6_concept_stock.groovy
call groovy 7_yearly_trading_summary.groovy
call groovy 8_monthly_closing_average_price.groovy
call groovy 9_monthly_trading_summary.groovy
call groovy 11_rankupdate.groovy
call groovy 13_short_sales_volume_and_value.groovy
call groovy 14_highlights_of_daily_trading.groovy
call groovy 15_foreign_mainland_area_investors_trading_and_shareholding.groovy
call groovy 16_daily_foreign_shareholding_by_industrial.groovy


set endtime=%time%
echo start at %startime%
echo end at %endtime%
pause