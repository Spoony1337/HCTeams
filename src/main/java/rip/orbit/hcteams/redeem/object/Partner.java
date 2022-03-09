package rip.orbit.hcteams.redeem.object;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Data;
import lombok.Getter;
import org.bson.Document;
import rip.orbit.hcteams.HCF;

import java.util.concurrent.CompletableFuture;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 7:33 PM
 * HCTeams / rip.orbit.hcteams.redeem
 */

@Data
public class Partner {

	@Getter public static MongoCollection<Document> collection = HCF.getInstance().getMongoPool().getDatabase("AllPartners").getCollection("partners");

	private String name;
	private int redeemedAmount = 0;

	public Partner(String name) {
		this.name = name;

		load();
	}

	public void load() {
		Document document = collection.find(Filters.eq("name", this.name)).first();

		if (document == null) return;

		this.redeemedAmount = document.getInteger("redeemedAmount");
	}

	public void save() {
		CompletableFuture.runAsync(() -> {

			Document document = new Document();

			document.put("name", this.name);
			document.put("redeemedAmount", this.redeemedAmount);

			collection.replaceOne(Filters.eq("name", this.name), document, new ReplaceOptions().upsert(true));
		});
	}

}
