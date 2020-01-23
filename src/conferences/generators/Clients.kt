package conferences.generators

import conferences.data.DataSets
import conferences.objects.Client
import conferences.objects.Company

object Clients {
    val clientList: List<Client> = List(DataSets.addresses.size) { createClient(it) }

    private fun createClient(currentClient: Int): Client {
        val phone = DataSets.phones[currentClient]
        val email = DataSets.emails[currentClient]
        val address = DataSets.addresses[currentClient]
        val company = if (Rand.current().nextBoolean()) createCompany(currentClient) else null
        return Client(email, phone, address, company)
    }

    private fun createCompany(currentClient: Int): Company {
        val name = DataSets.companyNames[currentClient]
        val address = DataSets.addresses[currentClient]
        val nip = DataSets.nips[currentClient] + currentClient
        return Company(nip, name, address)
    }
}