package ${service};

public interface ${name}Service {

    public PageResponse<${name}> list(Pagination page);

    public ${name} findById(${idType} id);

    public void update(${name} entity);

    public void save(${name} entity);

    public void remove(${idType} id);

    public List<${name}> findAll();

}
