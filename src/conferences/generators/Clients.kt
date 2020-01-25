package conferences.generators

import conferences.Rand
import conferences.data.DataSets
import conferences.objects.Client
import conferences.objects.Company

object Clients {
    val clientList: List<Client> = List(1000) { createClient() }

    private fun createClient(): Client {
        val phone = DataSets.get("phone")
        val email = DataSets.get("email")
        val address = DataSets.get("address")
        val company = if (Rand.current().nextBoolean()) createCompany() else null
        return Client(email, phone, address, company)
    }

    private fun createCompany(): Company {
        val name = DataSets.get("company_name")
        val address = DataSets.get("address")
        val nip = DataSets.get("NIP")
        return Company(nip, name, address)
    }
}