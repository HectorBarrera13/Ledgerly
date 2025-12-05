package toast.appback.src.groups.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import toast.appback.src.debts.application.usecase.contract.CreateDebtBetweenUsers;
import toast.appback.src.groups.application.usecase.implementation.AddMemberUseCase;
import toast.appback.src.groups.application.usecase.implementation.CreateGroupDebtUseCase;
import toast.appback.src.groups.application.usecase.implementation.CreateGroupUseCase;
import toast.appback.src.groups.application.usecase.implementation.EditGroupUseCase;
import toast.appback.src.groups.domain.repository.GroupDebtRepository;
import toast.appback.src.groups.domain.repository.GroupRepository;
import toast.appback.src.groups.domain.repository.MemberRepository;
import toast.appback.src.users.domain.repository.UserRepository;

@Configuration
public class GroupUseCasesConfig {

    @Bean
    public CreateGroupUseCase createGroupUseCase(
            GroupRepository groupRepository,
            UserRepository userRepository
    ) {
        return new CreateGroupUseCase(
                groupRepository,
                userRepository
        );
    }

    @Bean
    public EditGroupUseCase editGroupUseCase(
            GroupRepository groupRepository,
            UserRepository userRepository
    ) {
        return new EditGroupUseCase(
                groupRepository,
                userRepository
        );
    }

    @Bean
    public AddMemberUseCase addMemberUseCase(
            GroupRepository groupRepository,
            MemberRepository memberRepository,
            UserRepository userRepository
    ) {
        return new AddMemberUseCase(
                groupRepository,
                memberRepository,
                userRepository
        );
    }

    @Bean
    public CreateGroupDebtUseCase createGroupDebtUseCase(
            GroupDebtRepository groupDebtRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            CreateDebtBetweenUsers createDebtBetweenUsers
    ) {
        return new CreateGroupDebtUseCase(
                groupDebtRepository,
                groupRepository,
                userRepository,
                createDebtBetweenUsers
        );
    }

}
