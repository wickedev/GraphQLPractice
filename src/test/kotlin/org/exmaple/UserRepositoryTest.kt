package org.exmaple

import com.winterbe.expekt.should
import org.example.util.coroutine.flux.await
import org.example.util.coroutine.mono.await
import org.example.entity.User
import org.example.repository.R2dbcUserRepository
import org.example.repository.UserRepository
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UserRepositoryTest : Spek({
    describe("user repository") {
        val dbContainer = DatabaseContainer(this)

        val userRepository by memoized {
            UserRepository(
                dbContainer.getRepository(R2dbcUserRepository::class.java),
                dbContainer.r2dbcEntityTemplate,
                dbContainer.databaseClient
            )
        }

        beforeEachTest {
            dbContainer.create()
            dbContainer.populate("db/scheme.sql")
        }

        afterEachTest {
            dbContainer.destroy()
        }

        it("fixture user and saved user are the same") {
            val user = fixture<User>()
            val saved = userRepository.save(user).await()
            saved.email.should.be.equal(user.email)
            saved.name.should.be.equal(user.name)
        }

        it("fixture user and annotationFindBy are the same") {
            val user = fixture<User>()
            val saved = userRepository.save(user).await()
            val found = userRepository.annotationFindBy(saved.id!!).await()
                ?: throw Error("cannot find user by id")
            found.email.should.be.equal(user.email)
            found.name.should.be.equal(user.name)
        }

        it("fixture user and templateFindAll are the same") {
            val user = fixture<User>()
            userRepository.save(user).await()
            val users = userRepository.templateFindAll().await()
            users.size.should.be.at.least(1)
            users[0].email.should.be.equal(user.email)
            users[0].name.should.be.equal(user.name)
        }

        it("fixture user and templateFindAll are the same") {
            val user = fixture<User>()
            val saved = userRepository.save(user).await()
            val found = userRepository.rawSqlFindBy(saved.id!!).await()
                ?: throw Error("cannot find user by id")
            found.email.should.be.equal(user.email)
            found.name.should.be.equal(user.name)
        }
    }
})