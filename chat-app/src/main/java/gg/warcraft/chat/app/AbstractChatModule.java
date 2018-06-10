package gg.warcraft.monolith.app;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import gg.warcraft.monolith.api.command.CommandSender;
import gg.warcraft.monolith.api.command.Console;
import gg.warcraft.monolith.api.command.service.CommandCommandService;
import gg.warcraft.monolith.api.command.service.CommandQueryService;
import gg.warcraft.monolith.api.command.service.CommandRepository;
import gg.warcraft.monolith.api.config.JsonMapper;
import gg.warcraft.monolith.api.config.YamlMapper;
import gg.warcraft.monolith.api.core.EventService;
import gg.warcraft.monolith.api.effect.EffectVectors;
import gg.warcraft.monolith.api.effect.EffectVectorsFactory;
import gg.warcraft.monolith.api.entity.attribute.service.AttributeCommandService;
import gg.warcraft.monolith.api.entity.attribute.service.AttributeQueryService;
import gg.warcraft.monolith.api.entity.attribute.service.AttributeRepository;
import gg.warcraft.monolith.api.entity.status.service.StatusCommandService;
import gg.warcraft.monolith.api.entity.status.service.StatusQueryService;
import gg.warcraft.monolith.api.entity.status.service.StatusRepository;
import gg.warcraft.monolith.api.util.MathUtils;
import gg.warcraft.monolith.api.util.StringUtils;
import gg.warcraft.monolith.api.util.TimeUtils;
import gg.warcraft.monolith.api.world.service.WorldQueryService;
import gg.warcraft.monolith.app.command.ConsoleCommandSender;
import gg.warcraft.monolith.app.command.service.DefaultCommandCommandService;
import gg.warcraft.monolith.app.command.service.DefaultCommandQueryService;
import gg.warcraft.monolith.app.command.service.DefaultCommandRepository;
import gg.warcraft.monolith.app.config.JacksonJsonMapper;
import gg.warcraft.monolith.app.config.JacksonYamlMapper;
import gg.warcraft.monolith.app.core.GuavaEventService;
import gg.warcraft.monolith.app.effect.vectors.CircleVectors;
import gg.warcraft.monolith.app.effect.vectors.DomeVectors;
import gg.warcraft.monolith.app.effect.vectors.LineVectors;
import gg.warcraft.monolith.app.effect.vectors.OriginVector;
import gg.warcraft.monolith.app.effect.vectors.PointVector;
import gg.warcraft.monolith.app.effect.vectors.RingVectors;
import gg.warcraft.monolith.app.effect.vectors.SphereVectors;
import gg.warcraft.monolith.app.entity.attribute.service.DefaultAttributeCommandService;
import gg.warcraft.monolith.app.entity.attribute.service.DefaultAttributeQueryService;
import gg.warcraft.monolith.app.entity.attribute.service.DefaultAttributeRepository;
import gg.warcraft.monolith.app.entity.status.service.DefaultStatusCommandService;
import gg.warcraft.monolith.app.entity.status.service.DefaultStatusQueryService;
import gg.warcraft.monolith.app.entity.status.service.DefaultStatusRepository;
import gg.warcraft.monolith.app.util.DefaultMathUtils;
import gg.warcraft.monolith.app.util.DefaultStringUtils;
import gg.warcraft.monolith.app.util.DefaultTimeUtils;
import gg.warcraft.monolith.app.world.service.DefaultWorldQueryService;

public class AbstractMonolithModule extends AbstractModule {

    @Override
    protected void configure() {
        configureCommand();
        configureConfig();
        configureCore();
        configureEffect();
        configureEntity();
        configureUtil();
        configureWorld();
    }

    private void configureCommand() {
        bind(CommandCommandService.class).to(DefaultCommandCommandService.class);
        bind(CommandQueryService.class).to(DefaultCommandQueryService.class);
        bind(CommandRepository.class).to(DefaultCommandRepository.class);

        bind(CommandSender.class).annotatedWith(Console.class).to(ConsoleCommandSender.class);
    }

    private void configureConfig() {
        bind(JsonMapper.class).to(JacksonJsonMapper.class);
        bind(YamlMapper.class).to(JacksonYamlMapper.class);
    }

    private void configureCore() {
        bind(EventService.class).to(GuavaEventService.class).in(Singleton.class);
    }

    private void configureEffect() {
        install(new FactoryModuleBuilder()
                .implement(EffectVectors.class, Names.named("circle"), CircleVectors.class)
                .implement(EffectVectors.class, Names.named("dome"), DomeVectors.class)
                .implement(EffectVectors.class, Names.named("line"), LineVectors.class)
                .implement(EffectVectors.class, Names.named("origin"), OriginVector.class)
                .implement(EffectVectors.class, Names.named("point"), PointVector.class)
                .implement(EffectVectors.class, Names.named("ring"), RingVectors.class)
                .implement(EffectVectors.class, Names.named("sphere"), SphereVectors.class)
                .build(EffectVectorsFactory.class));
    }

    private void configureEntity() {
        bind(AttributeCommandService.class).to(DefaultAttributeCommandService.class);
        bind(AttributeQueryService.class).to(DefaultAttributeQueryService.class);
        bind(AttributeRepository.class).to(DefaultAttributeRepository.class);

        bind(StatusCommandService.class).to(DefaultStatusCommandService.class);
        bind(StatusQueryService.class).to(DefaultStatusQueryService.class);
        bind(StatusRepository.class).to(DefaultStatusRepository.class);
    }

    private void configureUtil() {
        bind(MathUtils.class).to(DefaultMathUtils.class);
        bind(StringUtils.class).to(DefaultStringUtils.class);
        bind(TimeUtils.class).to(DefaultTimeUtils.class);
    }

    private void configureWorld() {
        bind(WorldQueryService.class).to(DefaultWorldQueryService.class);
    }
}
