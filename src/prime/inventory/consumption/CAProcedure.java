package prime.inventory.consumption;
import java.sql.Connection;
import java.sql.Statement;

public class CAProcedure {

	String query;
	Statement st;
	ConnectionDB conobj;
	Connection connection=null;
		public CAProcedure()
	    {
			
	        conobj=new ConnectionDB();
  	        connection=conobj.getConnectionDB();
  	        
			production_table();
			USP_production_report_SP();
			SSV_StockConsumed_Mod_SP();
			SSB_StockConsumed_Mod_SP();
			ConsumptionDATA_SP();
	  
	    }
		
		public void production_table()
		{
			query="CREATE TABLE [dbo].[tbl_production](\r\n" + 
					"	[id] [int] IDENTITY(1,1) NOT NULL,\r\n" + 
					"	[lab] [varchar](50) NULL,\r\n" + 
					"	[order_received] [nvarchar](50) NULL,\r\n" + 
					"	[reissue] [nvarchar](50) NULL,\r\n" + 
					"	[rework] [nvarchar](50) NULL,\r\n" + 
					"	[Forder_received] [nvarchar](50) NULL,\r\n" + 
					"	[Freissue] [nvarchar](50) NULL,\r\n" + 
					"	[Frework] [nvarchar](50) NULL\r\n" + 
					") ON [PRIMARY]";

	        try {
	         st = connection.createStatement(); 
	         st.execute("drop table tbl_production");
	         st.close();
     		 st = connection.createStatement(); 
	         st.execute(query);
	        }catch(Exception e) {
	        	System.out.print(e);
	        	if(e.toString().equals("com.microsoft.sqlserver.jdbc.SQLServerException: Cannot drop the table 'tbl_production', because it does not exist or you do not have permission."))
	        	{
	        		try {
	        		st.close();
	        		st = connection.createStatement(); 
	   	         	st.execute(query);
	        		}catch(Exception ex) {
	        			System.out.print(e);
	        		}
	        	}
	        	
	        }
		}
		
		public void USP_production_report_SP()
		{
			query="create Procedure [dbo].[USP_production_report] @CityName varchar(50)\r\n" + 
					"As\r\n" + 
					"Begin\r\n" + 
					"\r\n" + 
					"DECLARE @CDate date;\r\n" + 
					"DECLARE @fromDate date;\r\n" + 
					"DECLARE @toDate date;\r\n" + 
					"\r\n" + 
					"DECLARE @order_received int;\r\n" + 
					"DECLARE @reissue int;\r\n" + 
					"DECLARE @rework int;\r\n" + 
					"\r\n" + 
					"DECLARE @Forder_received int;\r\n" + 
					"DECLARE @Freissue int;\r\n" + 
					"DECLARE @Frework int;\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"	set @CDate=GetDate();\r\n" + 
					"	set @fromDate=DATEADD(mm, DATEDIFF(mm, 0, @CDate) - 1, 0)\r\n" + 
					"	set @toDate=DATEADD (dd, -1, DATEADD(mm, DATEDIFF(mm, 0, @CDate) , 0))\r\n" + 
					"	\r\n" + 
					"	-- Consumption  1\r\n" + 
					"	Select @order_received=sum(cast(isnull(RQty,0) as int)) + sum(cast(isnull(LQty,0) as int))\r\n" + 
					"	from OrderMaster where G_OrderNo In (Select G_OrderNo from BlankRemoval where RemovalDate between @fromDate and @toDate)\r\n" + 
					"	And (LStock = 'L' or RStock = 'L') And G_OrderNo not in (Select G_ORderNo from CancelledOrders) \r\n" + 
					"	And Processed = 'Y' And Lens_Type not in (Select Lens_Type from LensGroupMaster where LensGroupName = 'FF')\r\n" + 
					"\r\n" + 
					"	-- ReIssue  2\r\n" + 
					"	Select @reissue=sum(cast(isnull(RQty,0) as int)) + sum(cast(isnull(LQty,0) as int))\r\n" + 
					"	from ReworkDefectMaster where (UpdateDate between @fromDate and @toDate) and ActionTaken like 'ReIssue%'\r\n" + 
					"	And G_OrderNo not in (Select G_ORderNo from CancelledOrders) \r\n" + 
					"	And Lens_Type not in (Select Lens_Type from LensGroupMaster where LensGroupName = 'FF')\r\n" + 
					"\r\n" + 
					"	-- ReWork  3\r\n" + 
					"	Select @rework=sum(cast(isnull(RQty,0) as int)) + sum(cast(isnull(LQty,0) as int))\r\n" + 
					"	from ReworkDefectMaster where (Order_Date between @fromDate and @toDate) and ActionTaken like '%ReWork%'\r\n" + 
					"	And G_OrderNo not in (Select G_ORderNo from CancelledOrders)\r\n" + 
					"	And Lens_Type not in (Select Lens_Type from LensGroupMaster where LensGroupName = 'FF')\r\n" + 
					"\r\n" + 
					"	--------- FF --------- Only In Mumbai, Delhi, Kolkata nd Bangalore -----\r\n" + 
					"	-- Consumption  1\r\n" + 
					"	Select @Forder_received=sum(cast(isnull(RQty,0) as int)) + sum(cast(isnull(LQty,0) as int))\r\n" + 
					"	from OrderMaster where G_OrderNo In (Select G_OrderNo from BlankRemoval where RemovalDate between @fromDate and @toDate)\r\n" + 
					"	And (LStock = 'L' or RStock = 'L') And G_OrderNo not in (Select G_ORderNo from CancelledOrders) \r\n" + 
					"	And Processed = 'Y' And Lens_Type in (Select Lens_Type from LensGroupMaster where LensGroupName = 'FF')\r\n" + 
					"\r\n" + 
					"	-- ReIssue  2\r\n" + 
					"	Select @Freissue=sum(cast(isnull(RQty,0) as int)) + sum(cast(isnull(LQty,0) as int))\r\n" + 
					"	from ReworkDefectMaster where (UpdateDate between @fromDate and @toDate) and ActionTaken like 'ReIssue%'\r\n" + 
					"	And G_OrderNo not in (Select G_ORderNo from CancelledOrders) \r\n" + 
					"	And Lens_Type in (Select Lens_Type from LensGroupMaster where LensGroupName = 'FF')\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"	-- ReWork  3\r\n" + 
					"	Select @Frework=sum(cast(isnull(RQty,0) as int)) + sum(cast(isnull(LQty,0) as int))\r\n" + 
					"	from ReworkDefectMaster where (Order_Date between @fromDate and @toDate) and ActionTaken like '%ReWork%'\r\n" + 
					"	And G_OrderNo not in (Select G_ORderNo from CancelledOrders)\r\n" + 
					"	And Lens_Type in (Select Lens_Type from LensGroupMaster where LensGroupName = 'FF')\r\n" + 
					"\r\n" + 
					"	--SELECT * FROM OPENQUERY([tallyserver], 'SELECT * FROM tallydb.dbo.tallypartybalance')\r\n" + 
					"	truncate table tbl_production\r\n" + 
					"	insert into tbl_production ([lab]\r\n" + 
					"      ,[order_received]\r\n" + 
					"      ,[reissue]\r\n" + 
					"      ,[rework]\r\n" + 
					"      ,[Forder_received]\r\n" + 
					"      ,[Freissue]\r\n" + 
					"      ,[Frework]) values(@CityName,@order_received,@reissue,@rework,@Forder_received,@Freissue,@Frework)\r\n" + 
					"	  \r\n" + 
					"	  \r\n" + 
					"	  --INSERT OPENQUERY (tallyserver, 'SELECT lab,order_received,reissue,rework,Forder_received,Freissue,Frework FROM tallyDB.dbo.tbl_production') VALUES ('003',@order_received,@reissue,@rework,@Forder_received,@Freissue,@Frework);  \r\n" + 
					"\r\n" + 
					"\r\n" + 
					"	--print @fromDate\r\n" + 
					"	--print @toDate\r\n" + 
					"		\r\n" + 
					"End";
	        try {
	         st = connection.createStatement(); 
	         st.execute("drop Procedure USP_production_report");
	         st.close();
     		 st = connection.createStatement(); 
	         st.execute(query);
	        }catch(Exception e) {
	        	System.out.print(e);
	        	if(e.toString().equals("com.microsoft.sqlserver.jdbc.SQLServerException: Cannot drop the procedure 'USP_production_report', because it does not exist or you do not have permission."))
	        	{
	        		try {
	        		st.close();
	        		st = connection.createStatement(); 
	   	         	st.execute(query);
	        		}catch(Exception ex) {
	        			System.out.print(e);
	        		}
	        	}
	        	
	        }
		}
		
		
		
		public void SSV_StockConsumed_Mod_SP()
		{
			query="create Procedure [dbo].[SSV_StockConsumed_Mod]\r\n" + 
					"As\r\n" + 
					"Begin\r\n" + 
					"DECLARE @CDate date;\r\n" + 
					"DECLARE @fromDate date;\r\n" + 
					"DECLARE @toDate date;\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"	set @CDate=GetDate();\r\n" + 
					"	set @fromDate=DATEADD(mm, DATEDIFF(mm, 0, @CDate) - 1, 0)\r\n" + 
					"	set @toDate=DATEADD (dd, -1, DATEADD(mm, DATEDIFF(mm, 0, @CDate) , 0))\r\n" + 
					"	\r\n" + 
					"	print @fromDate\r\n" + 
					"	print @toDate\r\n" + 
					"	Declare @lens_type as varchar(75), @qty as int\r\n" + 
					"	Truncate table stockconsumed_ssv\r\n" + 
					"	\r\n" + 
					"	Insert into stockconsumed_ssv(Lens_Type, Qty) \r\n" + 
					"	Select Lens_Type, sum(cast(isnull(RQty,0) as int)) + sum(cast(isnull(LQty,0) as int)) as Qty \r\n" + 
					"	from OrderMaster where G_OrderNo In (Select G_OrderNo from BlankRemoval where RemovalDate between @fromDate and @toDate)\r\n" + 
					"	and (stockOrder <> 'Y' or stockOrder is null) \r\n" + 
					"	And SingleVision_MultiFocal = 'SV' And Processed = 'Y'\r\n" + 
					"	Group by Lens_Type\r\n" + 
					"	Order by Lens_Type	\r\n" + 
					"	\r\n" + 
					"	Select Lens_Type, sum(cast(isnull(ReIssueQty,0) as int)) as Qty into tmp_ssvBlankReIssue\r\n" + 
					"	from BlankReIssue where G_OrderNo In (Select G_OrderNo from BlankRemoval where RemovalDate between @fromDate and @toDate)\r\n" + 
					"	and (stockOrder <> 'Y' or stockOrder is null) \r\n" + 
					"	And SingleVision_MultiFocal = 'SV' And Processed = 'Y' and (UpdateDate between @fromDate and @toDate)\r\n" + 
					"	Group by Lens_Type\r\n" + 
					"	Order by Lens_Type\r\n" + 
					"	\r\n" + 
					"	while(Select Count(*) from tmp_ssvBlankReIssue) > 0\r\n" + 
					"		Begin\r\n" + 
					"			Select Top 1 @lens_Type = Lens_Type, @qty = Qty from tmp_ssvBlankReIssue\r\n" + 
					"			\r\n" + 
					"			Update stockconsumed_ssv Set RQty = @qty where Lens_Type = @lens_type\r\n" + 
					"			\r\n" + 
					"			Delete from tmp_ssvBlankReIssue where Lens_type = @lens_Type\r\n" + 
					"		End\r\n" + 
					"	\r\n" + 
					"	Drop table tmp_ssvBlankReIssue\r\n" + 
					"	drop table SSV_consumed\r\n" + 
					"	Select Lens_Type, sum(cast(isnull(RQty,0) as int)) + sum(cast(isnull(Qty,0) as int)) as Qty into SSV_consumed from stockconsumed_ssv\r\n" + 
					"	group by Lens_Type Order by Lens_Type\r\n" + 
					"End";
	        try {
	         st = connection.createStatement(); 
	         st.execute("drop Procedure SSV_StockConsumed_Mod");
	         st.close();
     		 st = connection.createStatement(); 
	         st.execute(query);
	        }catch(Exception e) {
	        	System.out.print(e);
	        	if(e.toString().equals("com.microsoft.sqlserver.jdbc.SQLServerException: Cannot drop the procedure 'SSV_StockConsumed_Mod', because it does not exist or you do not have permission."))
	        	{
	        		try {
	        		st.close();
	        		st = connection.createStatement(); 
	   	         	st.execute(query);
	        		}catch(Exception ex) {
	        			System.out.print(e);
	        		}
	        	}
	        	
	        }
		}
		
		public void SSB_StockConsumed_Mod_SP()
		{
			query="create Procedure [dbo].[SSB_StockConsumed_Mod]\r\n" + 
					"As\r\n" + 
					"Begin\r\n" + 
					"\r\n" + 
					"DECLARE @CDate date;\r\n" + 
					"DECLARE @fromDate date;\r\n" + 
					"DECLARE @toDate date;\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"	set @CDate=GetDate();\r\n" + 
					"	set @fromDate=DATEADD(mm, DATEDIFF(mm, 0, @CDate) - 1, 0)\r\n" + 
					"	set @toDate=DATEADD (dd, -1, DATEADD(mm, DATEDIFF(mm, 0, @CDate) , 0))\r\n" + 
					"	\r\n" + 
					"	print @fromDate\r\n" + 
					"	print @toDate\r\n" + 
					"\r\n" + 
					"	Declare @lens_type as varchar(75), @qty as int		\r\n" + 
					"	Truncate table stockconsumed_ssb\r\n" + 
					"	Truncate table tmp_ssb\r\n" + 
					"	\r\n" + 
					"	Insert into tmp_ssb (Lens_Type, DummyType, Qty)\r\n" + 
					"	Select O.Lens_Type, isnull(M.Alternate_Lens_Type, O.Lens_Type) as DummyType, sum(cast(isnull(RQty,0) as int)) + sum(cast(isnull(LQty,0) as int)) as Qty\r\n" + 
					"	from OrderMaster O Left Join MapLensTypeForIPWideFreeForm M on O.Lens_Type = M.Lens_Type\r\n" + 
					"	where G_OrderNo In (Select G_OrderNo from BlankRemoval where RemovalDate between @fromDate and @toDate)\r\n" + 
					"	and (stockOrder <> 'Y' or stockOrder is null) \r\n" + 
					"	And SingleVision_MultiFocal = 'MF' And Processed = 'Y'\r\n" + 
					"	Group by O.Lens_Type, M.Alternate_Lens_Type\r\n" + 
					"	Order by O.Lens_Type\r\n" + 
					"		\r\n" + 
					"	Insert into stockconsumed_ssb(Lens_Type, Qty) \r\n" + 
					"	Select DummyType as Lens_Type, Sum(Qty) from tmp_ssb Group by DummyType Order by Lens_Type\r\n" + 
					"	\r\n" + 
					"	Select Lens_Type, sum(cast(isnull(ReIssueQty,0) as int)) as Qty into tmp_ssbBlankReIssue\r\n" + 
					"	from BlankReIssue where G_OrderNo In (Select G_OrderNo from BlankRemoval where RemovalDate between @fromDate and @toDate)\r\n" + 
					"	and (stockOrder <> 'Y' or stockOrder is null) \r\n" + 
					"	And SingleVision_MultiFocal = 'MF' And Processed = 'Y' and (UpdateDate between @fromDate and @toDate)\r\n" + 
					"	Group by Lens_Type\r\n" + 
					"	Order by Lens_Type\r\n" + 
					"	\r\n" + 
					"	Begin\r\n" + 
					"		UPDATE tmp_ssBBlankReIssue\r\n" + 
					"		SET Lens_Type = c2.Alternate_Lens_Type    \r\n" + 
					"		FROM tmp_ssBBlankReIssue c1\r\n" + 
					"		INNER JOIN MapLensTypeForIPWideFreeForm c2\r\n" + 
					"		ON c1.Lens_Type=c2.Lens_Type\r\n" + 
					"	End\r\n" + 
					"	\r\n" + 
					"	Select Lens_Type, Sum(Qty) as Qty into #Temp from tmp_ssBBlankReIssue Group by Lens_Type\r\n" + 
					"		\r\n" + 
					"	while(Select Count(*) from #Temp) > 0\r\n" + 
					"		Begin\r\n" + 
					"			Select Top 1 @lens_Type = Lens_Type, @qty = Qty from #Temp\r\n" + 
					"			\r\n" + 
					"			Update stockconsumed_ssb Set RQty = @qty where Lens_Type = @lens_type\r\n" + 
					"			\r\n" + 
					"			Delete from #Temp where Lens_type = @lens_Type\r\n" + 
					"		End\r\n" + 
					"	\r\n" + 
					"	Drop table #Temp\r\n" + 
					"	Truncate table tmp_ssbBlankReIssue\r\n" + 
					"	Drop table tmp_ssbBlankReIssue\r\n" + 
					"	drop table SSB_consumed\r\n" + 
					"\r\n" + 
					"	Select Lens_Type, sum(cast(isnull(RQty,0) as int)) + sum(cast(isnull(Qty,0) as int)) as Qty into ssb_consumed  from stockconsumed_ssb\r\n" + 
					"	group by Lens_Type Order by Lens_Type\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"		\r\n" + 
					"End";
	        try {
	         st = connection.createStatement(); 
	         st.execute("drop Procedure SSB_StockConsumed_Mod");
	         st.close();
     		 st = connection.createStatement(); 
	         st.execute(query);
	        }catch(Exception e) {
	        	System.out.print(e);
	        	if(e.toString().equals("com.microsoft.sqlserver.jdbc.SQLServerException: Cannot drop the procedure 'SSB_StockConsumed_Mod', because it does not exist or you do not have permission."))
	        	{
	        		try {
	        		st.close();
	        		st = connection.createStatement(); 
	   	         	st.execute(query);
	        		}catch(Exception ex) {
	        			System.out.print(e);
	        		}
	        	}
	        	
	        }
		}
		
		
		public void ConsumptionDATA_SP()
		{
			query="create PROCEDURE [dbo].[ConsumptionDATA]\r\n" + 
					"AS\r\n" + 
					"BEGIN\r\n" + 
					"DECLARE @CDate date;\r\n" + 
					"DECLARE @fromDate date;\r\n" + 
					"DECLARE @toDate date;\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"	set @CDate=GetDate();\r\n" + 
					"	set @fromDate=DATEADD(mm, DATEDIFF(mm, 0, @CDate) - 1, 0)\r\n" + 
					"	set @toDate=DATEADD (dd, -1, DATEADD(mm, DATEDIFF(mm, 0, @CDate) , 0))\r\n" + 
					"	\r\n" + 
					"	print @fromDate\r\n" + 
					"	print @toDate\r\n" + 
					"	exec SSV_StockConsumed_Mod\r\n" + 
					"	exec SSB_StockConsumed_Mod\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"	drop table FSV_consumed\r\n" + 
					"\r\n" + 
					"	Select Lens_Type+CoatColor lens_type, sum(cast(isnull(RQty,0) as int)) + sum(cast(isnull(LQty,0) as int)) as Qty into FSV_consumed\r\n" + 
					"	 from OrderMaster where G_OrderNo In (Select G_OrderNo from BlankRemoval where RemovalDate between @fromDate and @toDate) \r\n" + 
					"	 And SingleVision_MultiFocal = 'SV' And Processed = 'Y' And stockOrder = 'Y'\r\n" + 
					"	 Group by Lens_Type+CoatColor Order by Lens_Type+CoatColor\r\n" + 
					"\r\n" + 
					"	drop table FSVB_consumed\r\n" + 
					"	Select Lens_Type+CoatColor Lens_type, sum(cast(isnull(ReIssueQty,0) as int)) as Qty into FSVB_consumed\r\n" + 
					"	from BlankReIssue where G_OrderNo In (Select G_OrderNo from BlankRemoval where RemovalDate between @fromDate and @toDate)\r\n" + 
					"	 \r\n" + 
					"	And SingleVision_MultiFocal = 'SV' And Processed = 'Y' And stockOrder = 'Y' and (UpdateDate between @fromDate and @toDate)\r\n" + 
					"	Group by Lens_Type+CoatColor Order by Lens_Type+CoatColor\r\n" + 
					"\r\n" + 
					"	drop table FSB_consumed\r\n" + 
					"\r\n" + 
					"	Select Lens_Type+CoatColor Lens_type, sum(cast(isnull(RQty,0) as int)) + sum(cast(isnull(LQty,0) as int)) as Qty into FSB_consumed \r\n" + 
					"	from OrderMaster where G_OrderNo In (Select G_OrderNo from BlankRemoval where RemovalDate between @fromDate and @toDate)\r\n" + 
					"	And SingleVision_MultiFocal = 'MF' And Processed = 'Y' And stockOrder = 'Y'\r\n" + 
					"	Group by Lens_Type+CoatColor Order by Lens_Type+CoatColor	\r\n" + 
					"	\r\n" + 
					"	drop table FSBB_consumed	\r\n" + 
					"	Select Lens_Type+CoatColor lens_type, sum(cast(isnull(ReIssueQty,0) as int)) as Qty into FSBB_consumed\r\n" + 
					"	from BlankReIssue where G_OrderNo In (Select G_OrderNo from BlankRemoval where RemovalDate between @fromDate and @toDate)\r\n" + 
					"	 \r\n" + 
					"	And SingleVision_MultiFocal = 'MF' And Processed = 'Y' And stockOrder = 'Y' and (UpdateDate between @fromDate and @toDate)\r\n" + 
					"	Group by Lens_Type+CoatColor Order by Lens_Type+CoatColor\r\n" + 
					"END";
	        try {
	         st = connection.createStatement(); 
	         st.execute("drop Procedure ConsumptionDATA");
	         st.close();
     		 st = connection.createStatement(); 
	         st.execute(query);
	        }catch(Exception e) {
	        	System.out.print(e);
	        	if(e.toString().equals("com.microsoft.sqlserver.jdbc.SQLServerException: Cannot drop the procedure 'ConsumptionDATA', because it does not exist or you do not have permission."))
	        	{
	        		try {
	        		st.close();
	        		st = connection.createStatement(); 
	   	         	st.execute(query);
	        		}catch(Exception ex) {
	        			System.out.print(e);
	        		}
	        	}
	        	
	        }
		}
		
		
}
