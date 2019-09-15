package eu.redzoo.ml.deploy.ingest;

import com.google.common.collect.Lists;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;


public class Ingest {

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.out.println("usage Ingest <data in filename> <records out filename> <labels out filename>");
			System.exit(-1);
		}

		var sourceFilename = args[0].trim();   // e.g. the train csv of https://www.kaggle.com/c/house-prices-advanced-regression-techniques/data
		var recordsFilename = args.length > 1 ? args[1].trim() : "records.json";
		var labelsFilename = args.length > 2 ? args[2] .trim(): "labels.json";

		var sourcefile = new File(sourceFilename);
 		var lines = Files.readAllLines(sourcefile.toPath());
		var housesAndPrices = new HouseTrainDataLoader().load(House.class, lines);

		new ObjectMapper().writeValue(new File(recordsFilename), housesAndPrices.getLeft());
		new ObjectMapper().writeValue(new File(labelsFilename), housesAndPrices.getRight());

		System.out.println(new File(recordsFilename).getName() + ", " + new File(labelsFilename).getName() +
				           " created with " + housesAndPrices.getLeft().size() + " entries");
	}


	private static final class HouseTrainDataLoader {


		public Pair<List<House>, List<Double>> load(Class targetClass, List<String> lines) {
			List<House> houses = Lists.newArrayList();
			List<Double> prices = Lists.newArrayList();

			for (String line : lines) {
				try {
					String[] attributes = line.split(",");

					var house = new House();
					house.MSSubClass = readDouble(attributes, 1);
					house.MSZoning = readText(attributes, 2);
					house.LotFrontage = readDouble(attributes, 3);
					house.LotArea = readDouble(attributes, 4);
					house.Neighborhood = readText(attributes, 12);
					house.OverallQual = readDouble(attributes, 17);
					house.OverallCond = readDouble(attributes, 18);
					house.YearBuilt = readDouble(attributes, 19);
					house.YearRemodAdd = readDouble(attributes, 20);
					house.RoofStyle = readText(attributes, 21);
					house.BsmtQual = readText(attributes, 30);
					house.BsmtExposure = readText(attributes, 32);
					house.HeatingQC = readText(attributes, 40);
					house.CentralAir = readText(attributes, 41);
					house.FirstFlrSF = readDouble(attributes, 43);
					house.SecondFlrSF = readDouble(attributes, 44);
					house.GrLivArea = readDouble(attributes, 46);
					house.BsmtFullBath = readDouble(attributes, 47);
					house.BedroomAbvGr = readDouble(attributes, 51);
					house.KitchenQual = readText(attributes, 53);
					house.TotRmsAbvGrd = readDouble(attributes, 54);
					house.Fireplaces = readDouble(attributes, 56);
					house.FireplaceQu = readText(attributes, 57);
					house.GarageType = readText(attributes, 58);
					house.GarageFinish = readText(attributes, 60);
					house.GarageArea = readDouble(attributes, 62);
					house.MiscVal = readDouble(attributes, 75);
					house.YrSold = readDouble(attributes, 77);

					houses.add(house);

					prices.add(Double.parseDouble(attributes[80]));
				} catch (Exception e) {
					System.out.println("ignoring line " + line);
				}
			}
			return ImmutablePair.of(houses, prices);
		}

		private Double readDouble(String[] attributes, int pos) {
			return attributes[pos].equals("NA") ? null : Double.parseDouble(attributes[pos]);
		}

		private String readText(String[] attributes, int pos) {
			return attributes[pos].equals("NA") ? null : attributes[pos];
		}
	}


	public static class House {

		public  Double MSSubClass;

		public String MSZoning;

		public Double LotFrontage;

		public Double LotArea;

		public String Neighborhood;

		public Double OverallQual;

		public Double OverallCond;

		public Double YearBuilt;

		public Double YearRemodAdd;

		public String RoofStyle;

		public String BsmtQual;

		public String BsmtExposure;

		public String HeatingQC;

		public String CentralAir;

		public Double FirstFlrSF;

		public Double SecondFlrSF;

		public Double GrLivArea;

		public Double BsmtFullBath;

		public Double BedroomAbvGr;

		public String KitchenQual;

		public Double TotRmsAbvGrd;

		public Double Fireplaces;

		public String FireplaceQu;

		public String GarageType;

		public String GarageFinish;

		public Double GarageArea;

		public Double MiscVal;

		public Double YrSold;
	}
}