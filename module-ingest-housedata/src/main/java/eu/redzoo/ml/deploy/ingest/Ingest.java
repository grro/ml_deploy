package eu.redzoo.ml.deploy.ingest;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



public class Ingest {

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.out.println("usage Ingest <records out filename> <labels out filename>");
			System.exit(-1);
		}
		var recordsFilename = args.length > 1 ? args[1].trim() : "records.json";
		var labelsFilename = args.length > 2 ? args[2] .trim(): "labels.json";
		var excelSheetURL = new URL("http://jse.amstat.org/v19n3/decock/AmesHousing.xls");

		var numRows = new Ingest().process(excelSheetURL, recordsFilename, labelsFilename);

		System.out.println(new File(recordsFilename).getName() + ", " + new File(labelsFilename).getName() +
				" created with " + numRows + " entries");
	}

	public int process(URL excelSheetURL, String recordsFilename, String labelsFilename) throws IOException {
		var houses = new ExcelReader().read(excelSheetURL, House.class);

		new ObjectMapper().writeValue(new File(recordsFilename), houses);
		var prices = houses.stream().map(house -> house.SalePrice).collect(Collectors.toList());
		new ObjectMapper().writeValue(new File(labelsFilename), prices);

		return houses.size();
	}



	private static class ExcelReader {
		public <T> List<T> read(URL excelSheetURL, Class<T> clazz) {
			try (var is = excelSheetURL.openStream()) {
				return read(is, clazz);
			} catch (IOException ioe) {
				throw new UncheckedIOException(ioe);
			}
		}

		private <T> List<T> read(InputStream is, Class<T> clazz) throws IOException {
			List<T> rows = Lists.newArrayList();

			Iterator<Row> rowIterator = WorkbookFactory.create(is).getSheetAt(0).rowIterator();
			var headerRow = rowIterator.next();
			Map<String, Integer> columnNameIndex = Maps.newHashMap();
			for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
				columnNameIndex.put(headerRow.getCell(i).getStringCellValue(), i);
			}

			DataFormatter dataFormatter = new DataFormatter();
			while (rowIterator.hasNext()) {
				var row = rowIterator.next();
				try {
					var object = clazz.getDeclaredConstructor().newInstance();
					for (var field : clazz.getDeclaredFields()) {
						var columnName = ((Field) field.getAnnotation(Field.class)).name();
						var value = dataFormatter.formatCellValue(row.getCell(columnNameIndex.get(columnName)));
						try {
							if (Strings.isNullOrEmpty(value)) {
								field.set(object, null);
							} else {
								if (field.getType() == Double.class) {
									field.set(object, Double.valueOf(value));
								} else {
									field.set(object, value);
								}
							}
						} catch (IllegalAccessException ignore) {
						}
					}
					rows.add(object);
				} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
			return rows;
		}
	}


	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Field {
		public String name() default "";
	}


	public static class House {

		@Field(name = "MS SubClass")
		public Double MSSubClass;

		@Field(name = "MS Zoning")
		public String MSZoning;

		@Field(name = "Lot Frontage")
		public Double LotFrontage;

		@Field(name = "Lot Area")
		public Double LotArea;

		@Field(name = "Neighborhood")
		public String Neighborhood;

		@Field(name = "Overall Qual")
		public Double OverallQual;

		@Field(name = "Overall Cond")
		public Double OverallCond;

		@Field(name = "Year Built")
		public Double YearBuilt;

		@Field(name = "Year Remod/Add")
		public Double YearRemodAdd;

		@Field(name = "Roof Style")
		public String RoofStyle;

		@Field(name = "Bsmt Qual")
		public String BsmtQual;

		@Field(name = "Bsmt Exposure")
		public String BsmtExposure;

		@Field(name = "Heating QC")
		public String HeatingQC;

		@Field(name = "Central Air")
		public String CentralAir;

		@Field(name = "1st Flr SF")
		public Double FirstFlrSF;

		@Field(name = "2nd Flr SF")
		public Double SecondFlrSF;

		@Field(name = "Gr Liv Area")
		public Double GrLivArea;

		@Field(name = "Bsmt Full Bath")
		public Double BsmtFullBath;

		@Field(name = "Bedroom AbvGr")
		public Double BedroomAbvGr;

		@Field(name = "Kitchen Qual")
		public String KitchenQual;

		@Field(name = "TotRms AbvGrd")
		public Double TotRmsAbvGrd;

		@Field(name = "Fireplaces")
		public Double Fireplaces;

		@Field(name = "Fireplace Qu")
		public String FireplaceQu;

		@Field(name = "Garage Type")
		public String GarageType;

		@Field(name = "Garage Finish")
		public String GarageFinish;

		@Field(name = "Garage Area")
		public Double GarageArea;

		@Field(name = "Misc Val")
		public Double MiscVal;

		@Field(name = "Yr Sold")
		public Double YrSold;

		@Field(name = "SalePrice")
		public Double SalePrice;
	}
}
