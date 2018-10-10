package ${service};

import org.springframework.stereotype.Service;


@Service
public class ${name}ServiceImpl implements ${name}Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(${name}Service.class);

    @Autowired
    private ${name}Repository repository;

    @Autowired
    private ${name}QueryRepository queryRepository;

    @Override
    public PageResponse<${name}> list(Pagination pagination){
        PageResponse<${name}> resp = new PageResponse<${name}>();
        resp.setCount(queryRepository.count().intValue());
        resp.setData(queryRepository.list(pagination));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("${name} list  Result: {}", resp);
        }
        return resp;
    };

    @Override
    public ${name} findById(${idType} id){
        Optional< ${name}> e = repository.findById(id);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("${name} findById {} Result: {}",id, e.orElse(null));
        }
        return e.orElse(null);
    };

    @Override
    public void update(${name} entity){
        repository.saveAndFlush(entity);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("${name} update {}  ",entity);
        }
    };

    @Override
    public void save(${name} entity){
         repository.saveAndFlush(entity);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("${name} save {}  ",entity);
        }
    };

    @Override
    public void remove(${idType} id){
        repository.deleteById(id);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("${name} remove id:{}  ",id);
        }
    };

    @Override
    public List<${name}> findAll(){
        return repository.findAll();
    };

}
