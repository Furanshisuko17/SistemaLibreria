package com.utp.trabajo.services;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.entities.Cliente;
import com.utp.trabajo.services.security.SecurityService;
import com.utp.trabajo.services.transaction.ClienteTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//TODO: maybe refactor some implementations that can be more performant!
//TODO: Work in progress, still many bugs to be fixed!
/**
 * Clase que abstrae la base de datos y guarda un caché de datos de de los
 * Clientes
 *
 * @author Fran
 */
@Service
public class ClienteService {

    @Autowired
    private ClienteTransaction clienteTransaction;

    @Autowired
    private SecurityService securityService;

    //ConcurrentMap donde se guardan los clientes donde la Key es el idCliente y el value es el objeto Cliente
    private ConcurrentSkipListMap<Long, Cliente> cachedClientes;

    public ClienteService() {
        cachedClientes = new ConcurrentSkipListMap<>();
    }

    // Vacia completamente el cache
    private void clearCacheData() {
        cachedClientes.clear();
    }

    // reemplaza todo el cache por nuevos datos de la base de datos
    private void updateCacheData() {
        int size = cachedClientes.size();
        List<Cliente> clientes = clienteTransaction.streamClientes(0L, (long) size);
        clearCacheData();
        clientes
            .stream()
            .forEach((cliente) -> cachedClientes.put(cliente.getIdCliente(), cliente));
    }

    // Añade los datos a la cache desde la base de datos y luego se regresan como
    // una lista de clientes
    private List<Cliente> addCacheData(Long fromId, Long limit) {
        List<Cliente> nuevosClientes = clienteTransaction.streamClientes(fromId, limit);
        nuevosClientes
            .stream()
            .forEach((cliente) -> cachedClientes.put(cliente.getIdCliente(), cliente));

        return nuevosClientes;
    }

    // Añade un cliente al caché en caso de que no exista en el Map con el ID especificado, luego lo devuelve
    private Cliente addCacheData(Cliente cliente) {
        cachedClientes.put(cliente.getIdCliente(), cliente);
        return cliente;
    }

    // Añade la lista de clientes a la caché y luego la devuelve
    private List<Cliente> addCacheData(List<Cliente> clientes) {
        clientes
            .stream()
            .forEach((cliente) -> cachedClientes.put(cliente.getIdCliente(), cliente));

        return clientes;
    }

    // Edita un valor del caché basado en el cliente ingresado
    private void editCacheEntry(Cliente cliente) { //edita una sola entrada del caché
        cachedClientes.replace(cliente.getIdCliente(), cliente);
    }

    // Elimina un valor de la cache basado en el cliene ingresado
    private void deleteCacheEntry(Cliente cliente) { //borra una sola entrada del caché
        cachedClientes.remove(cliente.getIdCliente());
    }

    // Elimina los valores de la cache basado de la lista de idClientes ingresada
    private void deleteCacheEntry(List<Long> idClientes) {
        idClientes.stream()
            .forEach((idCliente) -> {
                cachedClientes.remove(idCliente);
            });
    }

    // Devuelve todo el caché en forma de una lista de clientes
    private List<Cliente> getAllCachedData() {
        return new ArrayList<Cliente>(cachedClientes.values());
    }

    // Devuelve el caché desde el ultimo id, en caso de no encontrar, se 
    // agregan los datos la caché desde la base de datos y luego se regresan.
    private List<Cliente> getCachedData(Long lastId, Long limit) {
        List<Cliente> clientes;
        if (cachedClientes.containsKey(lastId)) {
            ConcurrentNavigableMap<Long, Cliente> subMapClientes = cachedClientes
                .subMap(lastId,
                    true,
                    (lastId + limit),
                    true);

            clientes = new ArrayList<>(subMapClientes.values());
            return clientes;
        } else {
            clientes = addCacheData(lastId, limit);
            return clientes;
        }

    }

    private List<Cliente> getClientes(Long lastId, Long rowsPerUpdate) {
        List<Cliente> clientes;

        if (lastId == 0) {
            clientes = getAllCachedData();  
            if(!clientes.isEmpty()) {
                return clientes;
            } else {
                //get all data from database (add it to cache and return it)
            }
        }

        // Se verifica que existe el ultimo id + 1 y regresa todo el cache guardado
        if (cachedClientes.containsKey(lastId + 1L)) { 
            //get needed data from database(add it to cache and return only the needed data)
            clientes = getCachedData(lastId, rowsPerUpdate);
        } else { // actualiza el cache con info de la base de datos y luego lo regresa 
            clientes = getCachedData(lastId, rowsPerUpdate);
        }
        return clientes;
    }

    // Devuelve un Vector<Vector> listo para ser agregado al DefaultTableModel
    private Vector<Vector> getClientesAsVector(Long lastId, Long rowsPerUpdate) {
        List<Cliente> clientes = getClientes(lastId, rowsPerUpdate);
        Vector<Vector> clientesAsVector = new Vector<>();

        for (Cliente cliente : clientes) {
            Vector clienteAsVector = new Vector<>();
            clienteAsVector.add(cliente.getIdCliente());
            clienteAsVector.add(cliente.getNombre());
            clienteAsVector.add(cliente.getDireccion());
            clienteAsVector.add(cliente.getIdentificacion());
            clienteAsVector.add(cliente.getTelefono());
            clienteAsVector.add(cliente.getRazonSocial());
            clienteAsVector.add(cliente.getNumeroCompras());
            clientesAsVector.add(clienteAsVector);
        }
        return clientesAsVector;
    }

    // ↓ MÉTODOS PÚBLICOS ↓

    /**
     * Carga los clientes en el caché para ser cargados en la tabla despues
     * llamando al método {@code updateTable(...)}
     *
     * @param lastId ultimo idCliente de la tabla
     * @param rowsPerUpdate cantidad de filas a cargar cada que se desee cargar
     * más desde la base de datos.
     * @throws NotEnoughPermissionsException si no hay permisos de lectura.
     */
    public Vector<Vector> obtenerClientes(Long lastId, Long rowsPerUpdate) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        return getClientesAsVector(lastId, rowsPerUpdate);
    }

    /**
     * Obtiene un objeto <code>Cliente</code> con los datos del cliente
     * solicitado.
     *
     * @param id idCliente del cliente
     * @return el cliente agregado
     * @throws NotEnoughPermissionsException si no hay permisos de lectura.
     */
    public Cliente obtenerClientePorId(Long id) throws NotEnoughPermissionsException {

        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }

        if (cachedClientes.containsKey(id)) { // verifica que el cliente ya esté en la caché
            return cachedClientes.get(id);
        } else { // Si no está lo pide de la base de datos, lo guarda y lo devuelve
            Cliente cliente = clienteTransaction.encontrarClientePorId(id);
            addCacheData(cliente);
            return cliente;
        }
    }

    /**
     * Añade a la base de datos un nuevo cliente
     *
     * @param cliente El objeto tipo <code>Cliente</code> a agregar a la base de
     * datos
     * @return el <code>Cliente</code> agregado.
     * @throws NotEnoughPermissionsException si no hay permisos de creación.
     */
    public Cliente nuevoCliente(Cliente cliente) throws NotEnoughPermissionsException {

        if (!securityService.getPermissions().contains("create")) {
            throw new NotEnoughPermissionsException("Sin permisos de creación.");
        }

        Cliente clienteAgregado = clienteTransaction.nuevoCliente(cliente);
        addCacheData(cliente);
        return clienteAgregado;
    }

    /**
     * Actualiza un cliente ya existente Por debajo tiene la misma
     * implementación de <code>nuevoCliente(Cliente cliente)</code> pero se usa
     * en contextos diferentes.
     *
     * @param cliente El objeto tipo <code>Cliente</code> a actualizar
     * @return el <code>Cliente</code> actualizado
     * @throws NotEnoughPermissionsException si no hay permisos de edición.
     */
    public Cliente actualizarCliente(Cliente cliente) throws NotEnoughPermissionsException {

        if (!securityService.getPermissions().contains("create")) {
            throw new NotEnoughPermissionsException("Sin permisos de edición.");
        }

        Cliente clienteActualizado = clienteTransaction.actualizarCliente(cliente);
        editCacheEntry(clienteActualizado);
        return clienteActualizado;
    }

    /**
     * Elimina los clientes especificados en la lista de idClientes
     *
     * @param idCliente El objeto tipo {@code List<Long>} con los idCliente de
     * los clientes a eliminar
     * @return una {@code List<Cliente>} de los objetos eliminados.
     * @throws NotEnoughPermissionsException si no hay permisos de eliminación.
     */
    public List<Cliente> eliminarClientes(List<Long> idCliente) throws NotEnoughPermissionsException {

        if (!securityService.getPermissions().contains("delete")) {
            throw new NotEnoughPermissionsException("Sin permisos de eliminado.");
        }

        List<Cliente> clientesEliminados = clienteTransaction.eliminarClientes(idCliente);
        deleteCacheEntry(idCliente);
        return clientesEliminados;
    }

    /**
     * Método envolvente del método original en ClienteTransaction.
     *
     * @return la cantidad de clientes en la base de datos.
     * @throws com.utp.trabajo.exception.security.NotEnoughPermissionsException
     * si no hay permisos de lectura.
     */
    public long contarClientes() throws NotEnoughPermissionsException {

        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }

        return clienteTransaction.contarClientes();
    }

    /**
     * Método que devuelve la cantidad de clientes cacheados.
     *
     * @return la cantidad de clientes en el caché
     */
    public int contarClientesCacheados() {
        return cachedClientes.size();
    }
}
